package org.fh.gae.net.handler;

import com.alibaba.fastjson.JSON;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import org.fh.gae.net.vo.BidRequest;
import org.fh.gae.net.vo.BidResponse;
import org.fh.gae.net.vo.BidResult;
import org.fh.gae.query.BasicSearch;
import org.fh.gae.query.vo.Ad;
import org.fh.gae.query.vo.AdSlot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class GaeBidHandlerVertx implements Handler<RoutingContext> {
    @Autowired
    private BasicSearch bsSearchService;

    @Override
    public void handle(RoutingContext ctx) {
        BidRequest request = ctx.get("_req");
        long start = System.currentTimeMillis();

        List<Future> futList = buildAsyncTask(ctx, request);
        composeResultAndResponse(ctx, request, futList, start);
    }

    private List<Future> buildAsyncTask(RoutingContext ctx, BidRequest request) {
        Vertx vertx = ctx.vertx();

        // 构造Future
        List<io.vertx.core.Future> adFutureList = new ArrayList<>(request.getSlots().size());

        // 遍历每一个广告位
        for (AdSlot slot : request.getSlots()) {
            io.vertx.core.Future<Ad> f = io.vertx.core.Future.future();
            adFutureList.add(f);

            // 创建异步任务
            vertx.getOrCreateContext().runOnContext(v -> {
                Ad ad = bsSearchService.bidSlot(slot, null, request);
                f.complete(ad);
            });
        }

        return adFutureList;
    }

    private void composeResultAndResponse(RoutingContext ctx, BidRequest request, List<Future> adFutureList, long start) {
        // 组合异步任务结果
        CompositeFuture.all(adFutureList).setHandler(ar -> {
            if (ar.failed()) {
                Throwable cause = ar.cause();
                ctx.put("_err", cause);
                ctx.fail(500);

                return;
            }

            // 取出结果
            List<Ad> adList = adFutureList.stream()
                    .map(fut -> (Ad) fut.result())
                    .collect(Collectors.toList());

            // 构造响应对象
            BidResult result = new BidResult();
            result.setRequestId(request.getRequestId());
            result.setAds(adList);

            long end = System.currentTimeMillis();
            String json = JSON.toJSONString(new BidResponse(result, end - start));

            log.info("response = {}", json);
            ctx.response().end(json);
        });

    }
}
