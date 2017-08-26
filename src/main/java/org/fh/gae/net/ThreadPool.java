package org.fh.gae.net;

import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.fh.gae.config.GaeServerProps;
import org.fh.gae.net.utils.NettyUtils;
import org.fh.gae.net.vo.BidRequest;
import org.fh.gae.net.vo.BidResponse;
import org.fh.gae.query.BsSearchService;
import org.fh.gae.query.vo.Ad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
        pool = Executors.newFixedThreadPool(serverProps.getBusinessThreadPoolSize());
        log.info("business therad pool initialzed, size: {}", serverProps.getBusinessThreadPoolSize());

    }

    public void execute(final BidRequest bidRequest, final ChannelHandlerContext ctx) {
        pool.execute( () -> {
            List<Ad> adList = bsSearchService.fetchAds(bidRequest);
            ctx.writeAndFlush(NettyUtils.buildResponse(new BidResponse(adList)));
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
