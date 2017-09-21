package org.fh.gae.query.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;

public class GaeCollectionUtils {
    private GaeCollectionUtils() {

    }

    public static <T_KEY, T_INFO> Set<T_INFO> transform(Set<T_KEY> keys, Function<T_KEY, T_INFO> func) {
        if (null == keys || keys.isEmpty()) {
            return Collections.emptySet();
        }

        Set<T_INFO> infoSet = new HashSet<>();
        for (T_KEY key : keys) {
            T_INFO info = func.apply(key);
            if (null != info) {
                infoSet.add(info);
            }
        }

        return infoSet;
    }

    /**
     * 从Map中按key取值, 如果不存在则创建,入map, 并返回创建的对象
     * @param key
     * @param map
     * @param factory
     * @param <T_RET>
     * @param <T_KEY>
     * @return
     */
    public static <T_RET, T_KEY> T_RET getAndCreateIfNeed(T_KEY key, Map<T_KEY, T_RET> map, Supplier<T_RET> factory) {
        T_RET ret = map.get(key);
        if (null == ret) {
            ret = factory.get();
            map.put(key, ret);
        }

        return ret;
    }

    public static Set<Integer> randomNumbers(int max, int count) {
        if (max < 1) {
            return Collections.emptySet();
        }

        if (count >= max - 1) {
            Set<Integer> set = new HashSet<>(count + count / 3);
            for (int ix = 0; ix <= max; ++ix) {
                set.add(ix);
            }

            return set;
        }

        Set<Integer> set = new HashSet<>(count + count / 3);
        Random random = new Random();

        while (set.size() < count) {
            int n = random.nextInt(max + 1);
            set.add(n);
        }

        return set;
    }
}
