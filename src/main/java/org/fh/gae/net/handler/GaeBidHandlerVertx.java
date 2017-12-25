package org.fh.gae.net.handler;

import com.alibaba.fastjson.JSON;
import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.fh.gae.net.vo.BidResponse;
import org.fh.gae.net.vo.BidResult;
import org.fh.gae.query.BasicSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GaeBidHandlerVertx implements Handler<RoutingContext> {
    @Autowired
    private BasicSearch bsSearchService;

    @Override
    public void handle(RoutingContext ctx) {
        try {
            long start = System.currentTimeMillis();
            BidResult result = bsSearchService.bid(ctx.get("_req"));
            long end = System.currentTimeMillis();

            String json = JSON.toJSONString(new BidResponse(result, end - start));
            ctx.response().end(json);

            log.info(json);

        } catch (Exception e) {
            ctx.put("_err", e);
            ctx.fail(500);
        }

    }
}
