package org.fh.gae.query.index.region;

import org.fh.gae.query.index.GaeIndex;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 地域索引
 */
@Component
public class RegionIndex implements GaeIndex<RegionInfo> {
    public static final int LEVEL = 9;

    /**
     * 单元id->地域
     */
    private Map<Integer, RegionInfo> unitRegionMap;

    @PostConstruct
    public void init() {
        unitRegionMap = new ConcurrentHashMap<>();
    }


    @Override
    public int getLevel() {
        return LEVEL;
    }

    @Override
    public int getLength() {
        return 4;
    }

    @Override
    public RegionInfo packageInfo(String[] tokens) {
        Integer unitId = Integer.valueOf(tokens[2]);
        Integer l1Region = Integer.valueOf(tokens[3]);
        Integer l2Region = Integer.valueOf(tokens[4]);


        return new RegionInfo(unitId, l1Region, l2Region);
    }

    @Override
    public void add(RegionInfo regionInfo) {
        unitRegionMap.put(regionInfo.getUnitId(), regionInfo);
    }

    @Override
    public void update(RegionInfo regionInfo) {
        add(regionInfo);
    }

    @Override
    public void delete(RegionInfo regionInfo) {
        unitRegionMap.remove(regionInfo.getUnitId());
    }
}
