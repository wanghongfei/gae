package org.fh.gae.query.index.filter;

import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;

/**
 * 通用过虑器接口, 用来过虑推广单元或创意
 * @param <T>
 */
public interface GaeFilter<T> {
    void filter(Collection<T> elems);

    /**
     * 遍历要过虑的集合, 通过predicate判断是否保留当前元素
     * @param elems
     * @param judge predicate返回true表示保存, false为删除元素
     */
    default void traverse(Collection<T> elems, Predicate<T> judge) {
        if (null == elems || elems.isEmpty()) {
            return;
        }

        Iterator<T> it = elems.iterator();
        while (it.hasNext()) {
            boolean keep = judge.test(it.next());
            if (!keep) {
                it.remove();
            }
        }
    }
}
