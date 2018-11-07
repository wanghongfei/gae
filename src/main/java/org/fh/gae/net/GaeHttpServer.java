package org.fh.gae.net;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ResponseTimeHandler;
import lombok.extern.slf4j.Slf4j;
import org.fh.gae.config.GaeServerProps;
import org.fh.gae.net.handler.GaeAuthHandlerVertx;
import org.fh.gae.net.handler.GaeBidHandlerVertx;
import org.fh.gae.net.handler.GaeErrorHandlerVertx;
import org.fh.gae.net.handler.GaeJsonHandlerVertx;
import org.springframework.context.ApplicationContext;

@Slf4j
public class GaeHttpServer extends AbstractVerticle {
    public static ApplicationContext springCtx;

    private GaeJsonHandlerVertx jsonHandlerVertx;

    private GaeAuthHandlerVertx authHandlerVertx;

    private GaeBidHandlerVertx bidHandlerVertx;

    private GaeErrorHandlerVertx errorHandlerVertx;

    private GaeServerProps serverProps;

    public GaeHttpServer() {
        this.jsonHandlerVertx = springCtx.getBean(GaeJsonHandlerVertx.class);
        this.authHandlerVertx = springCtx.getBean(GaeAuthHandlerVertx.class);
        this.bidHandlerVertx = springCtx.getBean(GaeBidHandlerVertx.class);
        this.errorHandlerVertx = springCtx.getBean(GaeErrorHandlerVertx.class);
        this.serverProps = springCtx.getBean(GaeServerProps.class);
    }

    @Override
    public void start() {
        log.info("deploy GAE HTTP verticle");

        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route().handler(ResponseTimeHandler.create());
        router.route("/")
                .handler(jsonHandlerVertx)
                .handler(authHandlerVertx)
                // .blockingHandler(bidHandlerVertx, false)
                .handler(bidHandlerVertx)
                .failureHandler(errorHandlerVertx);

        server.requestHandler(router::accept).listen(serverProps.getPort());
    }

}
