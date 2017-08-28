package org.fh.gae.query.index;

public interface GaeIndex<T_KEY, T_INFO> {
    int getLevel();

    int getLength();

    T_INFO packageInfo(String[] tokens);

    void add(T_KEY key, T_INFO info);

    void update(T_KEY key, T_INFO info);

    void delete(T_KEY key, T_INFO info);
}
