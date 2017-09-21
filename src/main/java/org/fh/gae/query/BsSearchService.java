package org.fh.gae.query;

import lombok.extern.slf4j.Slf4j;
import org.fh.gae.net.vo.BidRequest;
import org.fh.gae.net.vo.BidResult;
import org.fh.gae.net.vo.RequestInfo;
import org.fh.gae.query.index.DataTable;
import org.fh.gae.query.index.filter.FilterTable;
import org.fh.gae.query.index.idea.IdeaIndex;
import org.fh.gae.query.index.idea.IdeaInfo;
import org.fh.gae.query.index.idea.UnitIdeaRelIndex;
import org.fh.gae.query.index.plan.PlanIndex;
import org.fh.gae.query.index.tag.TagIndex;
import org.fh.gae.query.index.unit.AdUnitIndex;
import org.fh.gae.query.index.unit.AdUnitInfo;
import org.fh.gae.query.profile.AudienceProfile;
import org.fh.gae.query.profile.ProfileFetcher;
import org.fh.gae.query.vo.Ad;
import org.fh.gae.query.vo.AdSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Slf4j
public class BsSearchService {
    public static final int MIN_UNIT_AMOUNT = 100;

    @Autowired(required = false)
    private ProfileFetcher profileFetcher;

    @Autowired
    private Picker picker;

    public BidResult bid(BidRequest request) {
        // 查画像
        AudienceProfile profile = null;
        if (null != profileFetcher) {
            profile = profileFetcher.fetchProfile(request);
        }

        List<Ad> adList = new ArrayList<>(request.getSlots().size());

        for (AdSlot slot : request.getSlots()) {
            RequestInfo req = new RequestInfo(request, slot);

            // 触发单元
            Set<Integer> unitIdSet = triggerUnit(profile);

            // 获取单元info
            Set<AdUnitInfo> unitInfoSet = DataTable.of(AdUnitIndex.class).fetchInfo(unitIdSet);

            // 过虑单元
            FilterTable.getFilter(AdUnitInfo.class).filter(unitInfoSet, req, profile);

            // 获取创意id
            Set<String> ideaIds = DataTable.of(UnitIdeaRelIndex.class).fetchIdeaIds(unitInfoSet);

            // 获取创意信息
            Set<IdeaInfo> ideaInfoSet = DataTable.of(IdeaIndex.class).fetchInfo(ideaIds);

            // 过虑创意
            FilterTable.getFilter(IdeaInfo.class).filter(ideaInfoSet, req, profile);

            if (!CollectionUtils.isEmpty(ideaInfoSet)) {
                List<IdeaInfo> infoList = ideaInfoSet.stream().collect(Collectors.toList());
                IdeaInfo target = picker.pickOne(infoList);
                adList.add(target.toAd(slot.getSlotId()));
            }
        }

        BidResult result = new BidResult();
        result.setRequestId(request.getRequestId());
        result.setAds(adList);

        return result;
    }

    private Set<Integer> triggerUnit(AudienceProfile profile) {
        Set<Integer> unitIdSet = new HashSet<>();

        // 使用画像触发
        if (null != profile) {
            unitIdSet = DataTable.of(TagIndex.class).triggerUnit(profile);
        }

        // 补量
        if (unitIdSet.size() < MIN_UNIT_AMOUNT) {
            Set<Integer> addedUnits = DataTable.of(AdUnitIndex.class).fetchRandom(MIN_UNIT_AMOUNT - unitIdSet.size());
            unitIdSet.addAll(addedUnits);
        }

        return unitIdSet;
    }

    private List<Ad> fetchAds(BidRequest request) {
        List<Ad> adList = new ArrayList<>();

        for (AdSlot slot : request.getSlots()) {
            Ad ad = new Ad();
            ad.setAdId("ad1");
            ad.setUrl("http://www.baidu.com");
            ad.setShowUrls(Arrays.asList("http://gae.com/a.gif?x=y").toArray(new String[0]));
            ad.setSlotId(slot.getSlotId());

            adList.add(ad);
        }

        return adList;
    }
}
