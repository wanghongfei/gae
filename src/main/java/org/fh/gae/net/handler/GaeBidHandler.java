package org.fh.gae.net.handler;

import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.fh.gae.net.ThreadPool;
import org.fh.gae.net.error.ErrCode;
import org.fh.gae.net.utils.NettyUtils;
import org.fh.gae.net.vo.BidRequest;
import org.fh.gae.net.vo.BidResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.RejectedExecutionException;

/**
 * bid请求处理器
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class GaeBidHandler extends ChannelInboundHandlerAdapter {
    @Autowired
    private ThreadPool pool;

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        BidRequest request = (BidRequest) msg;

        // 将广告请求提交到业务线程池中处理
        pool.execute(request, ctx);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage());

        if (cause instanceof RejectedExecutionException) {
            ctx.writeAndFlush(NettyUtils.buildResponse(BidResponse.errorOf(ErrCode.SERVER_BUSY)))
                    .addListener(ChannelFutureListener.CLOSE);
            return;
        }

        ctx.writeAndFlush(NettyUtils.buildResponse(BidResponse.errorOf(ErrCode.SERVER_ERROR)))
                .addListener(ChannelFutureListener.CLOSE);
    }
}
