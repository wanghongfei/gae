package org.fh.gae.net;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import lombok.extern.slf4j.Slf4j;
import org.fh.gae.config.GaeServerProps;
import org.fh.gae.net.coder.JsonRequestDecoder;
import org.fh.gae.net.handler.GaeAuthHandler;
import org.fh.gae.net.handler.GaeBidHandler;
import org.fh.gae.net.vo.BidRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GaeHttpServer {

    @Autowired
    private GaeBidHandler bidHandler;

    @Autowired
    private GaeAuthHandler authHandler;

    @Autowired
    private GaeServerProps serverProps;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    public void start() throws Exception {
        log.info("starting GAE server");

        bossGroup = new NioEventLoopGroup(1);
        workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap boot = new ServerBootstrap();
            boot.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(serverProps.getHost(), serverProps.getPort())
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("codec", new HttpServerCodec());
                            socketChannel.pipeline().addLast("aggregator", new HttpObjectAggregator(512 * 1024));
                            socketChannel.pipeline().addLast("bidDecoder", new JsonRequestDecoder<>(BidRequest.class));
                            socketChannel.pipeline().addLast("auth", authHandler);
                            socketChannel.pipeline().addLast("bid", bidHandler);
                        }
                    });

            ChannelFuture f = boot.bind().sync();

            log.info("GAE server started at {}:{}", serverProps.getHost(), serverProps.getPort());

            // f.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
            bossGroup.shutdownGracefully().sync();
        }
    }

    public void shutdown() throws Exception {
        bossGroup.shutdownGracefully().sync();

        log.info("GAE server has been stopped");
    }
}
