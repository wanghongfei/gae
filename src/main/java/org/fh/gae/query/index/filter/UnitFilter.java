package org.fh.gae.query.index.filter;

import org.fh.gae.net.vo.RequestInfo;
import org.fh.gae.query.index.DataTable;
import org.fh.gae.query.index.plan.PlanIndex;
import org.fh.gae.query.index.plan.PlanInfo;
import org.fh.gae.query.index.tag.TagIndex;
import org.fh.gae.query.index.tag.TagType;
import org.fh.gae.query.index.unit.AdUnitInfo;
import org.fh.gae.query.index.unit.AdUnitStatus;
import org.fh.gae.query.profile.AudienceProfile;
import org.fh.gae.query.session.ThreadCtx;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Component
@DependsOn("filterTable")
public class UnitFilter implements GaeFilter<AdUnitInfo> {

    @PostConstruct
    public void init() {
        FilterTable.register(AdUnitInfo.class, this);
    }

    @Override
    public void filter(Collection<AdUnitInfo> infos, RequestInfo request, AudienceProfile profile) {
        traverse(infos, info -> {
            boolean ok = isStatusFit(info) && isTagFit(info, profile);
            ok = ok && isPlanFit(info.getPlanId(), request, profile);

            return ok;
        });


    }

    private boolean isPlanFit(Integer planId, RequestInfo request, AudienceProfile profile) {
        PlanInfo planInfo = DataTable.of(PlanIndex.class).byId(planId);

        Set<PlanInfo> infoSet = new HashSet<>(3);
        infoSet.add(planInfo);

        FilterTable.getFilter(PlanInfo.class).filter(infoSet, request, profile);

        return !infoSet.isEmpty();
    }

    /**
     * 按单元状态过虑
     * @param info
     * @return
     */
    private boolean isStatusFit(AdUnitInfo info) {
        return info.getStatus() == AdUnitStatus.NORMAL;
    }

    /**
     * 按标签过虑
     * @return
     */
    private boolean isTagFit(AdUnitInfo info, AudienceProfile profile) {
        TagIndex tagIndex = DataTable.of(TagIndex.class);
        Map<Integer, Set<Long>> profileTagMap = profile == null ? Collections.emptyMap() : profile.getTagMap();

        Integer unitId = info.getUnitId();
        // 匹配性别, 或
        if (!isTagFit(profileTagMap, tagIndex, unitId, TagType.GENDER, true)) {
            return false;
        }

        // 匹配行业, 或
        if (!isTagFit(profileTagMap, tagIndex, unitId, TagType.INDUSTRY, true)) {
            return false;
        }


        return true;
    }

    /**
     * 标签匹配
     * @param profileTagMap 画像中的标签
     * @param tagIndex 标签索引
     * @param unitId 单元id
     * @param tagType 标签类型
     * @param or 是否以或逻辑匹配标签
     * @return
     */
    private boolean isTagFit(Map<Integer, Set<Long>> profileTagMap,
                             TagIndex tagIndex,
                             Integer unitId,
                             TagType tagType,
                             boolean or) {

        // 取出当前tag类型索引数据
        Map<Integer, Set<Long>> typeTags = tagIndex.byType(tagType.code());

        // 如果没有该类型的索引数据, 或当前推广单元没有选择该类型的标签
        if (CollectionUtils.isEmpty(typeTags)
                || CollectionUtils.isEmpty(typeTags.get(unitId))) {
            return true;
        }


        // 当前单元选择的标签
        Set<Long> selectedTags = typeTags.get(unitId);
        // 画像中的标签
        Set<Long> profileTags = profileTagMap.get(tagType.code());

        if (CollectionUtils.isEmpty(profileTags) && !CollectionUtils.isEmpty(selectedTags)) {
            return false;
        }

        // 标记变量, 表示画像中的标签是否被单元选择的标签完全包含
        boolean allIncluded = true;
        for (Long profileTag : profileTags) {
            // 如果画像中的当前标签被单元选择了
            if (selectedTags.contains(profileTag)) {
                // 如果是或逻辑
                if (or) {
                    tagType.processTrace(unitId);
                    return true;
                }
            } else {
                allIncluded = false;
            }
        }

        // 当逻辑为且, 且画像标签被完全包含时
        if (false == or && true == allIncluded) {
            tagType.processTrace(unitId);
            return true;
        }

        return false;
    }

}
