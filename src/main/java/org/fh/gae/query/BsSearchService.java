package org.fh.gae.query;

import lombok.extern.slf4j.Slf4j;
import org.fh.gae.net.vo.BidRequest;
import org.fh.gae.net.vo.BidResult;
import org.fh.gae.query.index.DataTable;
import org.fh.gae.query.index.plan.PlanIndex;
import org.fh.gae.query.vo.Ad;
import org.fh.gae.query.vo.AdSlot;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
public class BsSearchService {
    public BidResult bid(BidRequest request) {
        int level = DataTable.of(PlanIndex.class).getLevel();
        System.out.println(level);

        List<Ad> adList = fetchAds(request);

        BidResult result = new BidResult();
        result.setRequestId(request.getRequestId());
        result.setAds(adList);

        return result;
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
