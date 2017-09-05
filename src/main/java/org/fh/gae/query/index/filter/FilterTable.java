package org.fh.gae.query.index.filter;

import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class FilterTable {
    private static Map<Class, Object> table = new ConcurrentHashMap<>();

    public static <T> GaeFilter<T> getFilter(Class<T> type) {
        return (GaeFilter<T>) table.get(type);
    }

    public static void register(Class clazz, Object filter) {
        table.put(clazz, filter);
    }

}
