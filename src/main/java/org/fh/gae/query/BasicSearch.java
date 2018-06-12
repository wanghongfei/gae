package org.fh.gae.query;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.fh.gae.log.PbLogUtils;
import org.fh.gae.log.SearchLogWriter;
import org.fh.gae.net.vo.BidRequest;
import org.fh.gae.net.vo.BidResult;
import org.fh.gae.net.vo.RequestInfo;
import org.fh.gae.query.index.DataTable;
import org.fh.gae.query.index.filter.FilterTable;
import org.fh.gae.query.index.memory.idea.IdeaIndex;
import org.fh.gae.query.index.memory.idea.IdeaInfo;
import org.fh.gae.query.index.memory.idea.UnitIdeaRelIndex;
import org.fh.gae.query.index.memory.tag.TagIndex;
import org.fh.gae.query.index.memory.tag.TagType;
import org.fh.gae.query.index.memory.unit.AdUnitIndex;
import org.fh.gae.query.index.memory.unit.AdUnitInfo;
import org.fh.gae.query.profile.AudienceProfile;
import org.fh.gae.query.profile.ProfileFetcher;
import org.fh.gae.query.rank.Ranker;
import org.fh.gae.query.session.ThreadCtx;
import org.fh.gae.query.threadpool.GaeThreadPool;
import org.fh.gae.query.trace.TraceBit;
import org.fh.gae.query.vo.Ad;
import org.fh.gae.query.vo.AdSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class BasicSearch {
    @Value("${gae.min-unit-amount}")
    private int MIN_UNIT_AMOUNT;

    @Value("${gae.instance-id}")
    private byte instanceId;

    @Autowired(required = false)
    private ProfileFetcher profileFetcher;

    @Autowired
    private Ranker ranker;

    @Autowired
    private Picker picker;

    @Autowired
    private SearchLogWriter logWriter;

    @Autowired(required = false)
    private GaeThreadPool threadPool;

    public BidResult bid(BidRequest request) {
        // 查画像
        AudienceProfile profile = null;
        if (null != profileFetcher) {
            profile = profileFetcher.fetchProfile(request);
        }

        // AudienceProfile profile = mockProfile();

        // 广告结果List
        List<Ad> adList = new ArrayList<>(request.getSlots().size());

        // 如果只有一个广告位或没有threadpool, 串行检索
        if (null == threadPool || 1 == adList.size()) {
            serialQuery(request, profile, adList);

        } else {
            // 如果有多个广告位, 并发
            concurrentQueryIfNecessary(request, profile, adList);
        }


        BidResult result = new BidResult();
        result.setRequestId(request.getRequestId());
        result.setAds(adList);


        ThreadCtx.clean();

        return result;
    }

    private void serialQuery(BidRequest request, AudienceProfile profile, List<Ad> resultList) {
        for (AdSlot slot : request.getSlots()) {
            Ad ad = bidSlot(slot, profile, request);
            resultList.add(ad);
        }
    }

    private void concurrentQueryIfNecessary(BidRequest request, AudienceProfile profile, List<Ad> resultList) {
        List<Future<Ad>> adFutureList = new ArrayList<>(resultList.size());

        // 遍历每个广告位
        for (AdSlot slot : request.getSlots()) {
            // 提交任务
            Future<Ad> adFuture = threadPool.submitBidTask(new SlotBidTask(slot, profile, request), true);
            adFutureList.add(adFuture);
        }

        for (Future<Ad> adf : adFutureList) {
            try {
                // 最多等100ms
                Ad ad = adf.get(100L, TimeUnit.MILLISECONDS);
                resultList.add(ad);

            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                log.error("failed to bid concurrent slot, reason = {}", e);
            }
        }

    }

    private Set<Integer> triggerUnit(AudienceProfile profile) {
        Set<Integer> unitIdSet = new HashSet<>();

        // 使用画像触发
        if (null != profile) {
            unitIdSet.addAll(DataTable.of(TagIndex.class).triggerUnit(profile));
        }

        // 补量
        if (unitIdSet.size() < MIN_UNIT_AMOUNT) {
            Set<Integer> addedUnits = DataTable.of(AdUnitIndex.class).fetchRandom(MIN_UNIT_AMOUNT - unitIdSet.size());
            unitIdSet.addAll(addedUnits);
        }

        return unitIdSet;
    }

    private AudienceProfile mockProfile() {
        AudienceProfile profile = new AudienceProfile();
        Map<Integer, Set<Long>> tagMap = new HashMap<>();
        profile.setTagMap(tagMap);

        Set<Long> genderSet = new HashSet<>();
        genderSet.add(0L);
        tagMap.put(TagType.GENDER.code(), genderSet);

        Set<Long> indSet = new HashSet<>();
        indSet.add(0L);
        indSet.add(1L);
        indSet.add(2L);
        tagMap.put(TagType.INDUSTRY.code(), indSet);


        return profile;
    }

    private Ad bidSlot(AdSlot slot, AudienceProfile profile, BidRequest request) {
        // BenchTimer timer = new BenchTimer();

        RequestInfo req = new RequestInfo(request, slot);

        PbLogUtils.initSearchLog(req);
        // timer.recordTime("init-searchLog");

        // 触发单元
        Set<Integer> unitIdSet = triggerUnit(profile);
        // timer.recordTime("trigger-unit");

        // 获取单元info
        Set<AdUnitInfo> unitInfoSet = DataTable.of(AdUnitIndex.class).fetchInfo(unitIdSet);
        // timer.recordTime("fetch-unitinfo");

        // 过虑单元
        FilterTable.getFilter(AdUnitInfo.class).filter(unitInfoSet, req, profile);
        // timer.recordTime("filter-unitinfo");

        // 取权重最高单元
        unitInfoSet = ranker.rankUnitByWeight(unitInfoSet);
        // timer.recordTime("rank-unitinfo");

        // 获取创意id
        Set<String> ideaIds = DataTable.of(UnitIdeaRelIndex.class).fetchIdeaIds(unitInfoSet);
        // timer.recordTime("fetch-ideaid");

        // 获取创意信息
        Set<IdeaInfo> ideaInfoSet = DataTable.of(IdeaIndex.class).fetchInfo(ideaIds);
        // timer.recordTime("fetch-ideainfo");

        // 过虑创意
        FilterTable.getFilter(IdeaInfo.class).filter(ideaInfoSet, req, profile);
        // timer.recordTime("filter-ideainfo");

        // debug
        Map<Integer, TraceBit> map = ThreadCtx.getTraceMap();
        log.debug("traceMap = {}", map);
        Map<Integer, Integer> wMap = ThreadCtx.getWeightMap();
        log.debug("weightMap = {}", wMap);


        // 从创意结果中选择一个
        if (!CollectionUtils.isEmpty(ideaInfoSet)) {
            List<IdeaInfo> infoList = new ArrayList<>(ideaInfoSet);
            IdeaInfo target = picker.pickOne(infoList);

            // 构造Ad对象
            String slotId = slot.getSlotId();
            Ad ad = target.toAd(slotId);
            // adList.add(ad);

            // 取出单元信息记日志
            AdUnitInfo unit = ThreadCtx.getIdeaMap().get(ad.getAdId());
            Integer regionId = ThreadCtx.getUnitRegionMap().get(unit.getUnitId());

            PbLogUtils.updateAdInfo(
                    slotId,
                    ad,
                    unit.getPlanId(),
                    unit.getUnitId(),
                    ad.getAdId(),
                    instanceId,
                    regionId,
                    unit.getBid()
            );

            try {
                logWriter.writeLog(slotId);
                return ad;

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        return null;
    }

    @AllArgsConstructor
    private class SlotBidTask implements Callable<Ad> {
        private AdSlot slot;

        private AudienceProfile profile;

        private BidRequest request;

        @Override
        public Ad call() throws Exception {
            return bidSlot(slot, profile, request);
        }
    }
}
