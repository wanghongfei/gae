package org.fh.gae.query.trace;

import org.fh.gae.query.session.ThreadCtx;

import java.util.Map;

public interface Traceable {
    /**
     * 返回值永远是2的倍数, 对应二进制中只有一位为1,其它位为0的情况
     * @return
     */
    int getBitPosition();

    /**
     * 将推广单元的定向追踪值保存到线程上下文中
     * @param unitId 推广单元id
     */
    default void processTrace(Integer unitId) {
        Map<Integer, TraceBit> traceMap = ThreadCtx.getTraceMap();

        TraceBit traceBit = traceMap.get(unitId);
        int bitPosNumber = getBitPosition();

        if (null == traceBit) {
            traceMap.put(unitId, new TraceBit(bitPosNumber));

        } else {
            traceBit.bitOr(bitPosNumber);
        }
    }

    /**
     * 将推广单元的定向追踪值保存到线程上下文中
     * @param unitIds
     */
    default void processTrace(Iterable<Integer> unitIds) {
        Map<Integer, TraceBit> traceMap = ThreadCtx.getTraceMap();

        unitIds.forEach(unitId -> {
            TraceBit traceBit = traceMap.get(unitId);
            int bitPosNumber = getBitPosition();

            if (null == traceBit) {
                traceMap.put(unitId, new TraceBit(bitPosNumber));

            } else {
                traceBit.bitOr(bitPosNumber);
            }
        });
    }
}
