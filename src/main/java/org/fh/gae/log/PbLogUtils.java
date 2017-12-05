package org.fh.gae.log;

import org.fh.gae.net.vo.RequestInfo;
import org.fh.gae.query.session.ThreadCtx;
import org.fh.gae.query.vo.Ad;

import java.util.Date;

public class PbLogUtils {
    private PbLogUtils() {

    }

    /**
     * 创建log对象并保存到session
     * @param request
     */
    public static void initSearchLog(RequestInfo request) {
        SearchLog searchLog = new SearchLog();
        updateBasicInfo(searchLog, request);

        ThreadCtx.putSearchLog(request.getSlot().getSlotId(), searchLog);
    }

    /**
     * 填写日志对象中的基本请求字段
     * @param searchLog
     * @param request
     */
    public static void updateBasicInfo(SearchLog searchLog, RequestInfo request) {
        searchLog.setTid(request.getAuth().getTid());
        searchLog.setRequestId(request.getRequestId());
        searchLog.setTimestamp(new Date().getTime());

        searchLog.setMac(request.getDevice().getMac());
        searchLog.setIp(request.getDevice().getIp());
    }

    /**
     * 填写日志中的广告相关字段
     * @param slotId
     * @param ad
     * @param planId
     * @param unitId
     * @param ideaId
     */
    public static void updateAdInfo(String slotId, Ad ad, int planId, int unitId, String ideaId) {
        SearchLog searchLog = ThreadCtx.getSearchLogMap().get(slotId);

        searchLog.setWidth(ad.getW());
        searchLog.setHeight(ad.getH());
        searchLog.setMaterialType(ad.getMaterialType().toString());
        searchLog.setAdCode(ad.getAdId().toString());

        searchLog.setPlanId(planId);
        searchLog.setUnitId(unitId);
        searchLog.setIdeaId(ideaId);

        StringBuilder tagBuilder = ThreadCtx.getTagMap().get(slotId);
        if (null != tagBuilder) {
            searchLog.setTagIds(tagBuilder.toString());
        }
    }
}
