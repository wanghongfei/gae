package org.fh.gae.net.coder;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import lombok.extern.slf4j.Slf4j;
import org.fh.gae.net.utils.NettyUtils;
import org.fh.gae.net.vo.BidResponse;

import java.nio.charset.Charset;
import java.util.List;

/**
 * 将POST请求body反序列化成指定对象
 * @param <T>
 */
@Slf4j
public class JsonRequestDecoder<T> extends MessageToMessageDecoder<FullHttpRequest> {
    private Class<T> type;

    public JsonRequestDecoder(Class<T> type) {
        this.type = type;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, FullHttpRequest fullHttpRequest, List<Object> out) throws Exception {
        // 只允许POST请求
        boolean isPost = fullHttpRequest.method().name().equals("POST");
        if (false == isPost) {
            ctx.writeAndFlush(NettyUtils.buildResponse(HttpResponseStatus.METHOD_NOT_ALLOWED));
            ctx.close();
            return;
        }

        // 取出body
        byte[] body = fullHttpRequest.content().copy().array();

        log.info("gae_request\t{}", new String(body));

        // 反序列化
        T requestObj = JSON.parseObject(body, 0, body.length, Charset.forName("utf-8"), type);
        out.add(requestObj);

        log.debug("decode result: {}", requestObj);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        if (cause.getCause() instanceof JSONException) {
            log.warn("json parsing failed, {}", cause.getCause().getMessage());

        } else {
            cause.printStackTrace();
        }

        FullHttpResponse resp = NettyUtils.buildResponse(BidResponse.error(), HttpResponseStatus.BAD_REQUEST);
        ctx.writeAndFlush(resp).addListener(ChannelFutureListener.CLOSE);
    }
}
