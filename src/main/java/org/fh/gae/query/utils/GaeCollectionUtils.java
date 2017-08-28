package org.fh.gae.query.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.function.Function;

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

    public static <K, V> Set<V> getAndCreateIfNeed(K key, Map<K, Set<V>> map) {
        Set<V> set = map.get(key);
        if (null == set) {
            set = new ConcurrentSkipListSet<>();
            map.put(key, set);
        }

        return set;
    }
}
