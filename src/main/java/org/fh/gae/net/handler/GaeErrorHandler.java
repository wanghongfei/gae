package org.fh.gae.net.handler;

import com.alibaba.fastjson.JSON;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.fh.gae.net.error.GaeException;
import org.fh.gae.net.vo.BidResponse;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GaeErrorHandler implements Handler<RoutingContext> {

    @Override
    public void handle(RoutingContext ctx) {
        Exception e = ctx.get(ContextConst.EXCEPTION);

        if (e instanceof GaeException) {
            GaeException ex = (GaeException) e;
            ctx.response().end(JSON.toJSONString(new BidResponse(ex.code(), ex.getMessage())));

        } else {
            e.printStackTrace();
            ctx.response().setStatusCode(500);
            ctx.response().end("error");
        }
    }
}
