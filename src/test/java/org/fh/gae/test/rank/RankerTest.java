package org.fh.gae.test.rank;

import org.fh.gae.query.index.unit.AdUnitInfo;
import org.fh.gae.query.rank.Ranker;
import org.fh.gae.query.session.ThreadCtx;
import org.fh.gae.query.trace.TraceBit;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RankerTest {
    @Test
    public void testRank() {
        initSession();

        Set<AdUnitInfo> unitSet = initUnitData();
        Set<AdUnitInfo> resultSet = new Ranker().rankUnitByWeight(unitSet);
        System.out.println(resultSet);
    }

    private Set<AdUnitInfo> initUnitData() {
        Set<AdUnitInfo> unitSet = new HashSet<>();
        unitSet.add(new AdUnitInfo(0L, 0, 1, 0, 0L, 0));
        unitSet.add(new AdUnitInfo(0L, 0, 2, 0, 0L, 0));
        unitSet.add(new AdUnitInfo(0L, 0, 3, 0, 0L, 0));
        unitSet.add(new AdUnitInfo(0L, 0, 4, 0, 0L, 0));
        unitSet.add(new AdUnitInfo(0L, 0, 5, 0, 0L, 0));

        return unitSet;
    }

    private void initSession() {
        Map<Integer, TraceBit> traceMap = new HashMap<>();
        traceMap.put(1, new TraceBit(0x01));
        traceMap.put(2, new TraceBit(0x02));
        traceMap.put(3, new TraceBit(0x02));
        traceMap.put(4, new TraceBit(0x04));
        traceMap.put(5, new TraceBit(0x04));
        ThreadCtx.putContext(ThreadCtx.KEY_TARGETING_TRACE, traceMap);

    }
}
