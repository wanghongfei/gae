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
import org.fh.gae.query.BsSearchService;
import org.fh.gae.query.vo.Ad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class ThreadPool {
    private ExecutorService pool;

    @Autowired
    private GaeServerProps serverProps;

    @Autowired
    private BsSearchService bsSearchService;

    @PostConstruct
    public void initPool() {
        int configSize = serverProps.getBusinessThreadPoolSize();

        pool = new ThreadPoolExecutor(
                configSize,
                configSize * 10,
                30L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(configSize),
                new ThreadPoolExecutor.DiscardPolicy()
        );

        log.info("business thread pool initialzed, size: {}", serverProps.getBusinessThreadPoolSize());

    }

    public void execute(final BidRequest bidRequest, final ChannelHandlerContext ctx) {
        pool.execute( () -> {
            try {
                BidResult result = bsSearchService.bid(bidRequest);
                ctx.writeAndFlush(NettyUtils.buildResponse(new BidResponse(result)));

            } catch (Exception e) {
                e.printStackTrace();
                FullHttpResponse resp = NettyUtils.buildResponse(HttpResponseStatus.INTERNAL_SERVER_ERROR);
                ctx.writeAndFlush(resp);
            }

            ctx.close();

        } );
    }

    public void shudown(boolean gracefully) {
        if (gracefully) {
            pool.shutdown();

        } else {
            pool.shutdownNow();
        }
    }
}
