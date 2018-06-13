package org.fh.gae.query.index.unit;

import org.fh.gae.query.index.GaeIndex;
import org.fh.gae.query.utils.GaeCollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

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
        return 6;
    }

    public Set<AdUnitInfo> fetchInfo(Set<Integer> idSet) {
        Set<AdUnitInfo> resultSet = new HashSet<>(idSet.size() + idSet.size() / 3);

        idSet.forEach(id -> resultSet.add(adUnitInfoMap.get(id)));

        return resultSet;
    }

    public Set<Integer> fetchRandom(int amount) {
        if (amount <= 0) {
            return Collections.emptySet();
        }

        List<Integer> unitIdList = adUnitInfoMap.keySet().stream().collect(Collectors.toList());
        Set<Integer> randomIndex = GaeCollectionUtils.randomNumbers(unitIdList.size() - 1, amount);

        Set<Integer> resultSet = new HashSet<>(randomIndex.size() + randomIndex.size() / 3);
        for (Integer ix : randomIndex) {
            resultSet.add(unitIdList.get(ix));
        }

        return resultSet;
    }


    @Override
    public AdUnitInfo packageInfo(String[] tokens) {
        Integer unitId = Integer.valueOf(tokens[2]);
        Long userId = Long.valueOf(tokens[3]);
        Integer planId = Integer.valueOf(tokens[4]);
        Integer status = Integer.valueOf(tokens[5]);
        Long bid = Long.valueOf(tokens[6]);
        Integer priority = Integer.valueOf(tokens[7]);

        AdUnitInfo info = new AdUnitInfo();
        info.setUserId(userId);
        info.setUnitId(unitId);
        info.setPlanId(planId);
        info.setStatus(status);
        info.setBid(bid);
        info.setPriority(priority);

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
