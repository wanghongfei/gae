package org.fh.gae.net.coder;

import com.alibaba.fastjson.JSON;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.fh.gae.net.utils.NettyUtils;
import org.fh.gae.net.vo.BidRequest;
import org.fh.gae.net.vo.BidResponse;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.util.List;

/**
 * 将HTTP请求体中的json转换成BidRequest对象
 */
@Component
@ChannelHandler.Sharable
@Slf4j
public class BidRequestDecoder extends MessageToMessageDecoder<FullHttpRequest> {
    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest, List<Object> list) throws Exception {
        // 只允许POST请求
        boolean isPost = fullHttpRequest.method().equals("POST");
        if (false == isPost) {
            ctx.writeAndFlush(NettyUtils.buildResponse(HttpResponseStatus.METHOD_NOT_ALLOWED));
            ctx.close();
            return;
        }

        // 取出body
        byte[] body = fullHttpRequest.content().copy().array();

        // 反序列化
        BidRequest bidRequest = JSON.parseObject(body, 0, body.length, Charset.forName("utf-8"), BidRequest.class);
        list.add(bidRequest);

        log.debug("decode result: {}", bidRequest);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause.getMessage());

        ctx.writeAndFlush(NettyUtils.buildResponse(BidResponse.error()));
        ctx.close();
    }
}
