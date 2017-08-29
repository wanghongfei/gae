package org.fh.gae.query.index.tag;

import org.fh.gae.query.index.GaeIndex;
import org.fh.gae.query.utils.GaeCollectionUtils;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 标签索引
 */
@Component
public class TagIndex implements GaeIndex<TagInfo> {
    public static final int LELVE = 7;

    /**
     * [tag类型] -> [[单元id] -> [tag id]]
     */
    private Map<Integer, Map<Integer, Set<Long>>> typeUnitTagMap;

    /**
     * [tag id] -> [单元id集合]
     */
    private Map<Long, Set<Integer>> tagUnitMap;

    /**
     * [tag id] -> [tag信息]
     */
    private Map<Long, TagInfo> infoMap;

    @PostConstruct
    private void init() {
        infoMap = new ConcurrentHashMap<>();
        typeUnitTagMap = new ConcurrentHashMap<>();
        tagUnitMap = new ConcurrentHashMap<>();
    }

    @Override
    public int getLevel() {
        return LELVE;
    }

    @Override
    public int getLength() {
        return 3;
    }

    @Override
    public TagInfo packageInfo(String[] tokens) {
        Long tagId = Long.valueOf(tokens[2]);
        Integer unitId = Integer.valueOf(tokens[3]);
        Integer type = Integer.valueOf(tokens[4]);

        return new TagInfo(tagId, unitId, type);
    }


    @Override
    public void add(TagInfo tagInfo) {
        // 维护info索引
        infoMap.put(tagInfo.getTagId(), tagInfo);

        Integer unitId = tagInfo.getUnitId();
        Long tagId = tagInfo.getTagId();
        Integer type = tagInfo.getType();

        // 维护单元->标签索引
        Map<Integer, Set<Long>> unitTagMap = GaeCollectionUtils.getAndCreateIfNeed(
                type,
                typeUnitTagMap,
                () -> new HashMap<>()
        );
        Set<Long> tagSet = GaeCollectionUtils.getAndCreateIfNeed(
                unitId,
                unitTagMap,
                () -> new ConcurrentSkipListSet<>()
        );
        tagSet.add(tagId);


        // 维护tag id -> 单元倒排索引
        Set<Integer> unitSet = GaeCollectionUtils.getAndCreateIfNeed(
                tagId,
                tagUnitMap,
                () -> new ConcurrentSkipListSet<>()
        );
        unitSet.add(unitId);

    }

    @Override
    public void update(TagInfo tagInfo) {
        throw new IllegalStateException("cannot update tag index");
    }

    @Override
    public void delete(TagInfo tagInfo) {
        Integer unitId = tagInfo.getUnitId();
        Long tagId = tagInfo.getTagId();
        Integer type = tagInfo.getType();


        typeUnitTagMap.get(type).remove(unitId, tagId);
        tagUnitMap.get(tagId).remove(unitId);
    }
}
