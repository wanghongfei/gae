package org.fh.gae.query.index.unit;

import org.fh.gae.query.index.GaeIndex;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AdUnitIndex implements GaeIndex<AdUnitInfo> {
    public static final int LEVEL = 3;

    private Map<Integer, AdUnitInfo> adUnitInfoMap;

    @PostConstruct
    public void init() {
        adUnitInfoMap = new ConcurrentHashMap<>();
    }

    @Override
    public int getLevel() {
        return LEVEL;
    }

    @Override
    public int getLength() {
        return 5;
    }

    @Override
    public AdUnitInfo packageInfo(String[] tokens) {
        Integer unitId = Integer.valueOf(tokens[2]);
        Long userId = Long.valueOf(tokens[3]);
        Integer planId = Integer.valueOf(tokens[4]);
        Integer status = Integer.valueOf(tokens[5]);
        Long bid = Long.valueOf(6);

        AdUnitInfo info = new AdUnitInfo();
        info.setUserId(userId);
        info.setUnitId(unitId);
        info.setPlanId(planId);
        info.setStatus(status);
        info.setBid(bid);

        return info;
    }

    @Override
    public void add(AdUnitInfo adUnitInfo) {
        adUnitInfoMap.put(adUnitInfo.getUnitId(), adUnitInfo);
    }

    @Override
    public void update(AdUnitInfo adUnitInfo) {
        add(adUnitInfo);
    }

    @Override
    public void delete(AdUnitInfo adUnitInfo) {
        adUnitInfoMap.remove(adUnitInfo.getUnitId());
    }
}
