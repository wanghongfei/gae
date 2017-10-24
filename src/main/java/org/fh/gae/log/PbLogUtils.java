package org.fh.gae.log;

import org.fh.gae.net.vo.RequestInfo;
import org.fh.gae.query.session.ThreadCtx;
import org.fh.gae.query.vo.Ad;

import java.util.Date;

public class PbLogUtils {
    private PbLogUtils() {

    }

    public static SearchLog.Search.Builder makeSearchLogBuilder() {
        return SearchLog.Search.getDefaultInstance().toBuilder();
    }

    public static void initSearchLog(RequestInfo request) {
        SearchLog.Search.Builder pb = makeSearchLogBuilder();
        updateBasicInfo(pb, request);

        ThreadCtx.putSearchLog(request.getSlot().getSlotId(), pb);
    }

    public static void updateBasicInfo(SearchLog.Search.Builder pbLog, RequestInfo request) {
        pbLog.setTid(request.getAuth().getTid());
        pbLog.setRequestId(request.getRequestId());
        pbLog.setTimestamp(new Date().getTime());

        pbLog.setMac(request.getDevice().getMac());
        pbLog.setIp(request.getDevice().getIp());
    }

    public static void updateAdInfo(String slotId, Ad ad, int planId, int unitId, int ideaId) {
        SearchLog.Search.Builder pbLog = ThreadCtx.getSearchLogMap().get(slotId);

        pbLog.setWidth(ad.getW());
        pbLog.setHeight(ad.getH());
        pbLog.setMaterialType(ad.getMaterialType().toString());
        pbLog.setAdCode(ad.getAdId());

        pbLog.setPlanId(planId);
        pbLog.setUnitId(unitId);
        pbLog.setIdeaId(ideaId);
    }

    public static void updateProfileInfo(SearchLog.Search.Builder pbLog) {

    }
}
