package org.fh.gae.query;

import lombok.extern.slf4j.Slf4j;
import org.fh.gae.log.PbLogUtils;
import org.fh.gae.log.SearchLogWriter;
import org.fh.gae.net.vo.BidRequest;
import org.fh.gae.net.vo.BidResult;
import org.fh.gae.net.vo.RequestInfo;
import org.fh.gae.query.index.DataTable;
import org.fh.gae.query.index.filter.FilterTable;
import org.fh.gae.query.index.idea.IdeaIndex;
import org.fh.gae.query.index.idea.IdeaInfo;
import org.fh.gae.query.index.idea.UnitIdeaRelIndex;
import org.fh.gae.query.index.tag.TagIndex;
import org.fh.gae.query.index.tag.TagType;
import org.fh.gae.query.index.unit.AdUnitIndex;
import org.fh.gae.query.index.unit.AdUnitInfo;
import org.fh.gae.query.profile.AudienceProfile;
import org.fh.gae.query.profile.ProfileFetcher;
import org.fh.gae.query.rank.Ranker;
import org.fh.gae.query.session.ThreadCtx;
import org.fh.gae.query.trace.TraceBit;
import org.fh.gae.query.utils.BenchTimer;
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

@Component
@Slf4j
public class BasicSearch {
    @Value("${gae.min-unit-amount}")
    private int MIN_UNIT_AMOUNT;

    @Autowired(required = false)
    private ProfileFetcher profileFetcher;

    @Autowired
    private Ranker ranker;

    @Autowired
    private Picker picker;

    @Autowired
    private SearchLogWriter logWriter;

    public BidResult bid(BidRequest request) {
        // 查画像
        AudienceProfile profile = null;
        if (null != profileFetcher) {
            profile = profileFetcher.fetchProfile(request);
        }

        // AudienceProfile profile = mockProfile();

        List<Ad> adList = new ArrayList<>(request.getSlots().size());

        for (AdSlot slot : request.getSlots()) {
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
                adList.add(ad);

                AdUnitInfo unit = ThreadCtx.getIdeaMap().get(ad.getAdId());

                PbLogUtils.updateAdInfo(slotId, ad, unit.getPlanId(), unit.getUnitId(), ad.getAdId());

                try {
                    logWriter.writeLog(slotId);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

        BidResult result = new BidResult();
        result.setRequestId(request.getRequestId());
        result.setAds(adList);


        ThreadCtx.clean();

        return result;
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
}
