package org.fh.gae.query.session;

import io.netty.util.concurrent.FastThreadLocal;
import org.fh.gae.query.WeightTable;
import org.fh.gae.query.trace.TraceBit;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于线程的session
 */
public class ThreadCtx {
    private static FastThreadLocal<Map<String, Object>> threadLocal;

    static {
        threadLocal = new FastThreadLocal<>();
    }


    public static final String KEY_TARGETING_TRACE = "keyTargetingTrace";

    private static Map<String, Object> initContext() {
        Map<String, Object> map = new HashMap<>();
        threadLocal.set(map);

        return map;
    }

    public static void putContext(String key, Object val) {
        Map<String, Object> ctx = threadLocal.get();
        if (null == ctx) {
            ctx = initContext();
        }

        ctx.put(key, val);
    }

    public static <T> T getContext(String key) {
        Map<String, Object> ctx = threadLocal.get();
        if (null == ctx) {
            return null;
        }

        return (T) ctx.get(key);
    }

    public static void clean() {
        threadLocal.set(null);
    }


    public static Map<Integer, TraceBit> getTraceMap() {
        Map<Integer, TraceBit> map = getContext(KEY_TARGETING_TRACE);
        if (null == map) {
            map = new HashMap<>();
            putContext(KEY_TARGETING_TRACE, map);
        }

        return map;
    }

    /**
     * 获取单元权重Map;
     *
     * @return
     */
    public static Map<Integer, Integer> getWeightMap() {
        Map<Integer, TraceBit> traceMap = getTraceMap();

        Map<Integer, Integer> weightMap = new HashMap<>();
        for (Map.Entry<Integer, TraceBit> entry : traceMap.entrySet()) {
            Integer unitId = entry.getKey();
            TraceBit bit = entry.getValue();

            int weight = WeightTable.sum(bit.getBit());
            weightMap.put(unitId, weight);
        }

        return weightMap;
    }
}
