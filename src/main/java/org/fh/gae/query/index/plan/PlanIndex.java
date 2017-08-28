package org.fh.gae.query.index.plan;

import org.fh.gae.query.index.GaeIndex;
import org.fh.gae.query.utils.GaeCollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 推广计划索引
 */
@Component
public class PlanIndex implements GaeIndex<Integer, PlanInfo> {
    public static final int LEVEL = 2;

    private Map<Integer, PlanInfo> planInfoMap;

    @PostConstruct
    private void init() {
        planInfoMap = new ConcurrentHashMap<>();
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
    public PlanInfo packageInfo(String[] tokens) {
        Integer planId = Integer.valueOf(tokens[2]);
        Long userId = Long.valueOf(tokens[3]);
        Integer status = Integer.valueOf(tokens[4]);
        String timeBit = tokens[5];

        return new PlanInfo(planId, userId, status, timeBit);
    }

    @Override
    public void add(Integer key, PlanInfo planInfo) {
        planInfoMap.put(key, planInfo);
    }

    @Override
    public void update(Integer key, PlanInfo planInfo) {
        add(key, planInfo);
    }

    @Override
    public void delete(Integer key, PlanInfo planInfo) {
        planInfoMap.remove(key);
    }
}
