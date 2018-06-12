package org.fh.gae.net.handler;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.fh.gae.net.error.ErrCode;
import org.fh.gae.net.error.GaeException;
import org.fh.gae.net.vo.Auth;
import org.fh.gae.net.vo.BidRequest;
import org.fh.gae.query.index.memory.auth.AuthIndex;
import org.fh.gae.query.index.memory.auth.AuthInfo;
import org.fh.gae.query.index.memory.auth.AuthStatus;
import org.fh.gae.query.vo.AdSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
@Slf4j
public class GaeAuthHandlerVertx implements Handler<RoutingContext> {
    @Autowired
    private AuthIndex authIndex;

    @Override
    public void handle(RoutingContext ctx) {
        GaeException e = doAuth(ctx);
        if (null != e) {
            ctx.put("_err", e);
            ctx.fail(401);
        }
    }

    private GaeException doAuth(RoutingContext ctx) {
        BidRequest bidRequest = ctx.get("_req");

        // 取出授权字段
        Auth auth = bidRequest.getAuth();
        if (null == auth) {
            return new GaeException(ErrCode.NO_AUTH);
        }


        String tid = auth.getTid();
        String token = auth.getToken();

        if (StringUtils.isAnyEmpty(tid, token)) {
            return new GaeException(ErrCode.NO_AUTH);
        }

        // 检查tid是否存在
        AuthInfo info = authIndex.fetch(tid);
        if (null == info) {
            return new GaeException(ErrCode.NONE_EXIST);
        }

        // 检查是否黑
        if (AuthStatus.NORMAL != info.getStatus()) {
            return new GaeException(ErrCode.BLOCKED);
        }

        // 检查token
        if (false == StringUtils.equals(token, info.getToken())) {
            return new GaeException(ErrCode.INVALID_TOKEN);
        }

        // 检查请求参数
        if (StringUtils.isEmpty(bidRequest.getRequestId())) {
            return new GaeException(ErrCode.INVALID_ARG);
        }

        // 检查请求广告位信息
        List<AdSlot> slotList = bidRequest.getSlots();
        if (CollectionUtils.isEmpty(slotList)) {
            return new GaeException(ErrCode.INVALID_ARG);
        }

        for (AdSlot slot : slotList) {
            if (StringUtils.isEmpty(slot.getSlotId())
                    || slot.getSlotType() == null
                    || slot.getH() == null
                    || slot.getW() == null
                    || slot.getMaterialType() == null
                    || 0 == slot.getMaterialType().length) {
                return new GaeException(ErrCode.INVALID_ARG);
            }
        }

        ctx.next();

        return null;
    }
}
