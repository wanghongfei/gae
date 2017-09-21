package org.fh.gae.query.index.tag;

import org.fh.gae.query.index.GaeIndex;
import org.fh.gae.query.profile.AudienceProfile;
import org.fh.gae.query.utils.GaeCollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * 标签索引
 */
@Component
public class TagIndex implements GaeIndex<TagInfo> {
    public static final int LEVEL = 7;

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
        return LEVEL;
    }

    @Override
    public int getLength() {
        return 3;
    }

    public Map<Integer, Set<Long>> byType(Integer type) {
        return this.typeUnitTagMap.get(type);
    }

    /**
     * 根据画像中的标签触发单元
     * @param profile
     * @return
     */
    public Set<Integer> triggerUnit(AudienceProfile profile) {
        if (CollectionUtils.isEmpty(profile.getTagMap())) {
            return Collections.EMPTY_SET;
        }

        Set<Integer> resultSet = new HashSet<>();

        Collection<Set<Long>> typedTagSet = profile.getTagMap().values();
        for (Set<Long> profileTags : typedTagSet) {
            for (Long profileTag : profileTags) {
                Set<Integer> unitSet = tagUnitMap.get(profileTag);
                resultSet.addAll(unitSet);
            }
        }

        return resultSet;
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

        int i = 0;
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
