package org.fh.gae.query.rank;

import org.fh.gae.query.index.memory.unit.AdUnitInfo;
import org.fh.gae.query.session.ThreadCtx;
import org.fh.gae.query.utils.GaeCollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

@Service
public class Ranker {
    /**
     * 根据单元权重排序, 只返权重最高的单元集合
     * @param unitSet
     * @return
     */
    public Set<AdUnitInfo> rankUnitByWeight(Set<AdUnitInfo> unitSet) {
        if (CollectionUtils.isEmpty(unitSet)) {
            return Collections.emptySet();
        }

        Map<Integer, Integer> wMap = ThreadCtx.getWeightMap();
        TreeMap<Integer, Set<AdUnitInfo>> sortedMap = new TreeMap<>();

        for (AdUnitInfo unit : unitSet) {
            Integer unitId = unit.getUnitId();
            Integer weight = wMap.get(unitId);
            if (null == weight) {
                weight = Integer.valueOf(0);
            }

            Set<AdUnitInfo> infoSet = GaeCollectionUtils.getAndCreateIfNeed(
                    weight,
                    sortedMap,
                    () -> new HashSet<>()
            );

            infoSet.add(unit);
        }

        return sortedMap.lastEntry().getValue();
    }
}
