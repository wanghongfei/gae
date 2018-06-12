package org.fh.gae.query.index.memory.region;

import lombok.extern.slf4j.Slf4j;
import org.fh.gae.query.index.GaeIndex;
import org.fh.gae.query.utils.GaeCollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 地域索引
 */
@Component
@Slf4j
public class RegionIndex implements GaeIndex<RegionInfo> {
    public static final int LEVEL = 9;

    /**
     * 单元id->地域
     */
    private Map<Integer, Set<RegionInfo>> unitRegionMap;

    private Map<RegionInfo, Set<Integer>> regionUnitMap;

    @PostConstruct
    public void init() {
        unitRegionMap = new ConcurrentHashMap<>();
        regionUnitMap = new ConcurrentHashMap<>();
    }

    public Set<RegionInfo> getRegion(Integer unitId) {
        return unitRegionMap.get(unitId);
    }


    @Override
    public int getLevel() {
        return LEVEL;
    }

    @Override
    public int getLength() {
        return 3;
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
        Set<RegionInfo> infoSet = GaeCollectionUtils.getAndCreateIfNeed(
                regionInfo.getUnitId(),
                unitRegionMap,
                () -> new ConcurrentSkipListSet<>()
        );

        infoSet.add(regionInfo);
    }

    @Override
    public void update(RegionInfo regionInfo) {
        log.error("region cannot be updated");
    }

    @Override
    public void delete(RegionInfo regionInfo) {
        Set<RegionInfo> infoSet = GaeCollectionUtils.getAndCreateIfNeed(
                regionInfo.getUnitId(),
                unitRegionMap,
                () -> new ConcurrentSkipListSet<>()
        );

        infoSet.remove(regionInfo);
    }
}
