package org.fh.gae.query.index.idea;

import org.fh.gae.query.index.GaeIndex;
import org.fh.gae.query.utils.GaeCollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
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
