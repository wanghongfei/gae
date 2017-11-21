package org.fh.gae.net;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.fh.gae.config.GaeServerProps;
import org.fh.gae.net.utils.NettyUtils;
import org.fh.gae.net.vo.BidRequest;
import org.fh.gae.net.vo.BidResponse;
import org.fh.gae.net.vo.BidResult;
import org.fh.gae.query.BasicSearch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Component
@Slf4j
public class ThreadPool {
    private ExecutorService pool;

    @Autowired
    private GaeServerProps serverProps;

    @Autowired
    private BasicSearch bsSearchService;

    @PostConstruct
    public void initPool() {
        int configSize = serverProps.getBusinessThreadPoolSize();

        pool = new ThreadPoolExecutor(
                serverProps.getMinBizThread(),
                serverProps.getMaxBizThread(),
                30L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(configSize),
                new ThreadPoolExecutor.DiscardPolicy()
        );

        log.info("business thread pool initialized, size: {}", serverProps.getBusinessThreadPoolSize());

    }

    public void execute(final BidRequest bidRequest, final ChannelHandlerContext ctx) {
        pool.execute( () -> {
            try {
                long start = System.currentTimeMillis();
                BidResult result = bsSearchService.bid(bidRequest);
                // BidResult result = doExecute(bidRequest);
                long end = System.currentTimeMillis();

                ctx.writeAndFlush(NettyUtils.buildResponse(new BidResponse(result, end - start)));

            } catch (Exception e) {
                e.printStackTrace();
                FullHttpResponse resp = NettyUtils.buildResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                ctx.writeAndFlush(resp);
            }

            ctx.close();

        } );
    }

    private BidResult doExecute(BidRequest bidRequest) {
        List<BidResult> resultList = executeMultiSlots(bidRequest);
        return mergeResults(resultList);
    }

    private BidResult mergeResults(List<BidResult> results) {
        if (CollectionUtils.isEmpty(results)) {
            return null;
        }

        BidResult result = results.get(0);
        int len = results.size();
        if (len > 1) {
            for (int ix = 1; ix < len; ++ix) {
                result.merge(results.get(ix));
            }
        }

        return result;
    }

    private List<BidResult> executeMultiSlots(BidRequest bidRequest) {
        List<BidRequest> reqList = bidRequest.splitBySlot();

        List<Future<BidResult>> resultFutureList = new ArrayList<>(reqList.size());
        reqList.forEach( req -> {
            Future<BidResult> f = pool.submit( () -> bsSearchService.bid(req));
            resultFutureList.add(f);
        } );

        List<BidResult> resultList = new ArrayList<>(reqList.size());
        resultFutureList.forEach( f -> {
            try {
                BidResult result = f.get(1L, TimeUnit.SECONDS);
                resultList.add(result);

            } catch (InterruptedException e) {
                e.printStackTrace();

            } catch (ExecutionException e) {
                e.printStackTrace();

            } catch (TimeoutException e) {
                e.printStackTrace();
            }
        } );

        return resultList;
    }

    public void shudown(boolean gracefully) {
        if (gracefully) {
            pool.shutdown();

        } else {
            pool.shutdownNow();
        }
    }
}
