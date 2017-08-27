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
import org.fh.gae.net.coder.BidRequestDecoder;
import org.fh.gae.net.handler.GaeAuthHandler;
import org.fh.gae.net.handler.GaeBidHandler;
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
    private BidRequestDecoder bidRequestDecoder;

    @Autowired
    private GaeServerProps serverProps;

    private EventLoopGroup group;

    public void start() throws Exception {
        log.info("starting GAE server");

        group = new NioEventLoopGroup();

        try {
            ServerBootstrap boot = new ServerBootstrap();
            boot.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(serverProps.getHost(), serverProps.getPort())
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast("codec", new HttpServerCodec());
                            socketChannel.pipeline().addLast("aggregator", new HttpObjectAggregator(512 * 1024));
                            socketChannel.pipeline().addLast("bidDeocder", bidRequestDecoder);
                            socketChannel.pipeline().addLast("auth", authHandler);
                            socketChannel.pipeline().addLast("bid", bidHandler);
                        }
                    });

            ChannelFuture f = boot.bind().sync();

            log.info("GAE server started at {}:{}", serverProps.getHost(), serverProps.getPort());

            // f.channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
            group.shutdownGracefully().sync();
        }
    }

    public void shutdown() throws Exception {
        group.shutdownGracefully().sync();

        log.info("GAE server has been stopped");
    }
}
