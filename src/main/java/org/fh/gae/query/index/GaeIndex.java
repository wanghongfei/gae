package org.fh.gae.query.index;

import java.util.Set;

public interface GaeIndex<T_KEY, T_INFO> {
    int getLevel();

    int getLength();

    T_INFO packageInfo(String[] tokens);

    Set<T_INFO> trigger(int amount);

    Set<T_INFO> trigger(Object condition);

    void add(T_KEY key, T_INFO info);

    void update(T_KEY key, T_INFO info);

    void delete(T_KEY key, T_INFO info);
}
