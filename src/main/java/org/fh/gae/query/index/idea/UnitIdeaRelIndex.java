package org.fh.gae.query.index.idea;

import org.fh.gae.query.index.GaeIndex;
import org.fh.gae.query.index.unit.AdUnitInfo;
import org.fh.gae.query.session.ThreadCtx;
import org.fh.gae.query.utils.GaeCollectionUtils;
import org.omg.CORBA.INTERNAL;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Component
public class UnitIdeaRelIndex implements GaeIndex<UnitIdeaRelInfo> {
    public static final int LEVEL = 5;

    private Map<Integer, Set<String>> unitIdeaMap;

    @PostConstruct
    private void init() {
        unitIdeaMap = new ConcurrentHashMap<>();
    }

    @Override
    public int getLevel() {
        return LEVEL;
    }

    @Override
    public int getLength() {
        return 2;
    }

    public Set<String> fetchIdeaIds(Set<AdUnitInfo> unitInfoSet) {
        Set<String> resultSet = new HashSet<>(unitInfoSet.size() + unitInfoSet.size() / 3);

        Map<Integer, Integer> wMap = ThreadCtx.getWeightMap();
        Map<String, AdUnitInfo> ideaInfoMap = new HashMap<>();
        ThreadCtx.putContext(ThreadCtx.KEY_IDEA, ideaInfoMap);

        for (AdUnitInfo unitInfo : unitInfoSet ) {
            // 得到单元下所有创意
            Set<String> idSet = unitIdeaMap.get(unitInfo.getUnitId());
            if (!CollectionUtils.isEmpty(idSet)) {
                resultSet.addAll(idSet);

                // 如果一个创意被多个单元绑定
                // 则出权重最高的单元下的创意
                for (String ideaId : idSet) {
                    AdUnitInfo info = ideaInfoMap.get(ideaId);
                    if (null == info) {
                        ideaInfoMap.put(ideaId, unitInfo);
                    } else {
                        Integer oldWeight = wMap.get(info.getUnitId());
                        Integer newWeight = wMap.get(unitInfo.getUnitId());

                        if (null != oldWeight && null != newWeight) {
                            ideaInfoMap.put(ideaId, newWeight > oldWeight ? unitInfo :info);
                        }

                    }
                }
            }
        }

        return resultSet;
    }

    @Override
    public UnitIdeaRelInfo packageInfo(String[] tokens) {
        Integer unitId = Integer.valueOf(tokens[2]);
        String ideaId = tokens[3];

        return new UnitIdeaRelInfo(unitId, ideaId);
    }

    @Override
    public void add(UnitIdeaRelInfo info) {
        Set<String> ideaSet = GaeCollectionUtils.getAndCreateIfNeed(
                info.getUnitId(),
                unitIdeaMap,
                () -> new ConcurrentSkipListSet<>()
        );

        ideaSet.add(info.getIdeaId());
    }

    @Override
    public void update(UnitIdeaRelInfo info) {
        throw new IllegalStateException("unit idea relation index cannot be updated");
    }

    @Override
    public void delete(UnitIdeaRelInfo info) {
        unitIdeaMap.get(info.getUnitId()).remove(info.getIdeaId());
    }
}
