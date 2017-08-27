package org.fh.gae.net.utils;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import org.fh.gae.net.vo.BidResponse;

public class NettyUtils {
    private NettyUtils() {

    }

    public static FullHttpResponse buildResponse(BidResponse bidResponse) {
        byte[] buf = JSON.toJSONBytes(bidResponse);

        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                HttpResponseStatus.OK,
                Unpooled.wrappedBuffer(buf)
        );

        response.headers().set(
                HttpHeaderNames.CONTENT_TYPE.toString(),
                "application/json;charset=utf8"
        );

        return response;
    }

    public static FullHttpResponse buildResponse(HttpResponseStatus status) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1,
                status
        );

        return response;
    }
}
