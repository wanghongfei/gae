package org.fh.gae.query.index;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 内存数据表
 */
@Component
public class DataTable implements ApplicationContextAware {
    private static Map<Class, Object> table = new ConcurrentHashMap<>();

    private static ApplicationContext appCtx;

    public static <T> T of(Class<T> clazz) {
        T index = (T) table.get(clazz);
        if (null == index) {
            index = (T) register(appCtx.getBean(clazz));
        }

        return index;
    }

    private static Object register(Object index) {
        table.put(index.getClass(), index);

        return index;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        appCtx = applicationContext;
    }
}
