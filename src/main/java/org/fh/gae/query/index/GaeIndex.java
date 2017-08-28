package org.fh.gae.query.index;

public interface GaeIndex<T_INFO> {
    int getLevel();

    int getLength();

    T_INFO packageInfo(String[] tokens);

    void add(T_INFO info);

    void update(T_INFO info);

    void delete(T_INFO info);
}
