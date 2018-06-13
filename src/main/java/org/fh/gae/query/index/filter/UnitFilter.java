package org.fh.gae.query.index.filter;

import lombok.extern.slf4j.Slf4j;
import org.fh.gae.net.vo.RequestInfo;
import org.fh.gae.query.index.DataTable;
import org.fh.gae.query.index.plan.PlanIndex;
import org.fh.gae.query.index.plan.PlanInfo;
import org.fh.gae.query.index.region.IPRegion;
import org.fh.gae.query.index.region.RegionDict;
import org.fh.gae.query.index.region.RegionIndex;
import org.fh.gae.query.index.region.RegionInfo;
import org.fh.gae.query.index.tag.TagIndex;
import org.fh.gae.query.index.tag.TagType;
import org.fh.gae.query.index.unit.AdUnitInfo;
import org.fh.gae.query.index.unit.AdUnitStatus;
import org.fh.gae.query.profile.AudienceProfile;
import org.fh.gae.query.session.ThreadCtx;
import org.springframework.beans.factory.annotation.Autowired;
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
@Slf4j
public class UnitFilter implements GaeFilter<AdUnitInfo> {
    @Autowired
    private RegionDict regionDict;

    @PostConstruct
    public void init() {
        FilterTable.register(AdUnitInfo.class, this);
    }

    @Override
    public void filter(Collection<AdUnitInfo> infos, RequestInfo request, AudienceProfile profile) {
        traverse(infos, info -> isStatusFit(info) && isTagFit(info, profile, request)
                && isPlanFit(info.getPlanId(), request, profile)
                && isRegionFit(request, info.getUnitId()));


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
    private boolean isTagFit(AdUnitInfo info, AudienceProfile profile, RequestInfo request) {
        TagIndex tagIndex = DataTable.of(TagIndex.class);
        Map<Integer, Set<Long>> profileTagMap = profile == null ? Collections.emptyMap() : profile.getTagMap();

        Integer unitId = info.getUnitId();
        String slotCode = request.getSlot().getSlotId();
        // 匹配性别, 或
        if (!isTagFit(profileTagMap, tagIndex, unitId, TagType.GENDER, true, slotCode)) {
            return false;
        }

        // 匹配行业, 或
        if (!isTagFit(profileTagMap, tagIndex, unitId, TagType.INDUSTRY, true, slotCode)) {
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
                             boolean or,
                             String slotCode) {

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
                    ThreadCtx.addTag(slotCode, tagType.code(), profileTag);

                    return true;
                }
            } else {
                allIncluded = false;
            }
        }

        // 当逻辑为且, 且画像标签被完全包含时
        if (false == or && true == allIncluded) {
            tagType.processTrace(unitId);
            ThreadCtx.addTags(slotCode, tagType.code(), profileTags);
            return true;
        }

        return false;
    }

    /**
     * 匹配地域
     * @param request
     * @param unitId
     * @return
     */
    private boolean isRegionFit(RequestInfo request, Integer unitId) {
        // 解析ip
        String ip = request.getDevice().getIp();
        IPRegion ipRegion = regionDict.match(ip);

        Set<RegionInfo> infos = DataTable.of(RegionIndex.class).getRegion(unitId);
        boolean isTargetingChosen = !CollectionUtils.isEmpty(infos);
        // 没有选择地域定向
        if (!isTargetingChosen) {
            return true;
        }

        if (null == ipRegion) {
            log.warn("unidentified ip:{}", ip);
            return false;
        }

        int ipL1 = ipRegion.getL1Region();
        int ipL2 = ipRegion.getL2Region();
        log.debug("ip = {}, l1 = {}, l2 = {}", ip, ipL1, ipL2);

        for (RegionInfo info : infos) {
            int l1 = info.getL1Region();
            int l2 = info.getL2Region();

            if (l1 == ipL1) {
                if (l2 == ipL2 || -1 == l2) {
                    // 命中
                    ThreadCtx.getUnitRegionMap().put(unitId, ipL1);
                    return true;
                }
            }

        }

        log.debug("filter by region, unit = {}", unitId);
        return false;
    }

}
