package org.fh.gae.query.session;

import java.util.HashMap;
import java.util.Map;

/**
 * 基于线程的session
 */
public class ThreadCtx {
    private static ThreadLocal<Map<String, Object>> threadLocal;

    static {
        threadLocal = new ThreadLocal<>();
    }

    /**
     * 推广单元权重
     */
    public static final String KEY_UNIT_WEIGHT = "keyUnitWeight";

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

    /**
     * 将指定推广单元的权重增加指定值
     * @param unitId
     * @param newWeight
     */
    public static void addWeight(Integer unitId, Integer newWeight) {
        Map<Integer, Integer> unitWeightMap = getContext(KEY_UNIT_WEIGHT);
        if (null == unitWeightMap) {
            unitWeightMap = new HashMap<>();
            putContext(KEY_UNIT_WEIGHT, unitWeightMap);
        }

        Integer oldWeight = unitWeightMap.get(unitId);
        unitWeightMap.put(unitId, oldWeight == null ? newWeight : oldWeight + newWeight);
    }
}
