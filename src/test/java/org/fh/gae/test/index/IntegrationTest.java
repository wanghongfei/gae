package org.fh.gae.test.index;

import org.fh.gae.net.vo.BidRequest;
import org.fh.gae.net.vo.BidResult;
import org.fh.gae.query.BasicSearch;
import org.fh.gae.query.profile.AudienceProfile;
import org.fh.gae.query.profile.ProfileFetcher;
import org.fh.gae.test.BaseTestClass;
import org.fh.gae.test.RequestBuilder;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class IntegrationTest extends BaseTestClass {
    @Autowired
    private BasicSearch bs;

    /**
     * index/common
     */
    @Test
    public void testNoTargetingAndMaterialType() {
        BidRequest req = RequestBuilder.buildRequest();
        req.getDevice().setIp("45.124.44.68");
        req.getSlots().get(0).setMaterialType(new Integer[] {2});
        BidResult result = bs.bid(req);

        System.out.println(result);

        Assert.assertNotNull(result.getAds());
        Assert.assertFalse(result.getAds().isEmpty());
        Assert.assertEquals("idea2", result.getAds().get(0).getAdId());
        Assert.assertEquals("adCode2", result.getAds().get(0).getAdCode());
    }

    /**
     * /index/tag
     */
    @Test
    public void testTagTargetingAndWeight() {
        BidRequest req = RequestBuilder.buildRequest();
        req.getDevice().setIp("45.124.44.68");
        req.getSlots().get(0).setMaterialType(new Integer[] {1});

        // 实现ProfileFetcher
        ProfileFetcher fetcher = (request) -> {
            AudienceProfile profile = new AudienceProfile();
            Map<Integer, Set<Long>> tagMap = new HashMap<>();
            Set<Long> tagSet = new HashSet<>();
            tagSet.add(5000L);
            tagMap.put(1, tagSet);

            profile.setTagMap(tagMap);

            return profile;
        };


        // 通过反射将fetcher设置到basicSearch中
        Field field = ReflectionUtils.findField(BasicSearch.class, "profileFetcher");
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, bs, fetcher);

        BidResult result = bs.bid(req);
        System.out.println(result);


        Assert.assertNotNull(result.getAds());
        Assert.assertFalse(result.getAds().isEmpty());
        Assert.assertEquals("idea1", result.getAds().get(0).getAdId());



        // 实现ProfileFetcher
        fetcher = (request) -> {
            AudienceProfile profile = new AudienceProfile();
            Map<Integer, Set<Long>> tagMap = new HashMap<>();
            Set<Long> tagSet = new HashSet<>();
            tagSet.add(6001L);
            tagMap.put(0, tagSet);

            profile.setTagMap(tagMap);

            return profile;
        };
        ReflectionUtils.setField(field, bs, fetcher);

        result = bs.bid(req);
        System.out.println(result);


        Assert.assertNotNull(result.getAds());
        Assert.assertFalse(result.getAds().isEmpty());
        Assert.assertEquals("idea2", result.getAds().get(0).getAdId());



        // 权重测试
        // 实现ProfileFetcher
        fetcher = (request) -> {
            AudienceProfile profile = new AudienceProfile();
            Map<Integer, Set<Long>> tagMap = new HashMap<>();
            Set<Long> tagSet = new HashSet<>();
            tagSet.add(6001L);
            tagMap.put(0, tagSet);

            Set<Long> tagSet2 = new HashSet<>();
            tagSet2.add(5001L);
            tagMap.put(1, tagSet2);

            profile.setTagMap(tagMap);

            return profile;
        };
        ReflectionUtils.setField(field, bs, fetcher);

        result = bs.bid(req);
        System.out.println(result);


        Assert.assertNotNull(result.getAds());
        Assert.assertFalse(result.getAds().isEmpty());
        Assert.assertEquals("idea1", result.getAds().get(0).getAdId());
    }

    /**
     * index/region
     */
    @Test
    public void testRegionTargeting() {
        BidRequest req = RequestBuilder.buildRequest();
        req.getDevice().setIp("45.124.44.68"); // 北京IP
        req.getSlots().get(0).setMaterialType(new Integer[] {1});


        BidResult result = bs.bid(req);
        System.out.println(result);


        Assert.assertNotNull(result.getAds());
        Assert.assertFalse(result.getAds().isEmpty());
        Assert.assertEquals("idea1", result.getAds().get(0).getAdId());


        req.getDevice().setIp("114.80.166.240"); // 上海IP
        result = bs.bid(req);
        System.out.println(result);


        Assert.assertNotNull(result.getAds());
        Assert.assertFalse(result.getAds().isEmpty());
        Assert.assertEquals("idea2", result.getAds().get(0).getAdId());
    }
}
