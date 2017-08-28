package org.fh.gae.query.index.plan;

import org.fh.gae.query.index.GaeIndex;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 推广计划索引
 */
@Component
public class PlanIndex implements GaeIndex<PlanInfo> {
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
    public void add(PlanInfo planInfo) {
        planInfoMap.put(planInfo.getPlanId(), planInfo);
    }

    @Override
    public void update(PlanInfo planInfo) {
        add(planInfo);
    }

    @Override
    public void delete(PlanInfo planInfo) {
        planInfoMap.remove(planInfo.getPlanId());
    }
}
