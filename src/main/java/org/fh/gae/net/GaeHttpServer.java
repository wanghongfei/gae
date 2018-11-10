package org.fh.gae.net;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.ResponseTimeHandler;
import lombok.extern.slf4j.Slf4j;
import org.fh.gae.config.GaeServerProps;
import org.fh.gae.net.handler.GaeAuthHandler;
import org.fh.gae.net.handler.GaeBidHandler;
import org.fh.gae.net.handler.GaeErrorHandler;
import org.fh.gae.net.handler.GaeJsonHandler;
import org.springframework.context.ApplicationContext;

@Slf4j
public class GaeHttpServer extends AbstractVerticle {
    public static ApplicationContext springCtx;

    private GaeJsonHandler jsonHandlerVertx;

    private GaeAuthHandler authHandlerVertx;

    private GaeBidHandler bidHandlerVertx;

    private GaeErrorHandler errorHandlerVertx;

    private GaeServerProps serverProps;

    public GaeHttpServer() {
        this.jsonHandlerVertx = springCtx.getBean(GaeJsonHandler.class);
        this.authHandlerVertx = springCtx.getBean(GaeAuthHandler.class);
        this.bidHandlerVertx = springCtx.getBean(GaeBidHandler.class);
        this.errorHandlerVertx = springCtx.getBean(GaeErrorHandler.class);
        this.serverProps = springCtx.getBean(GaeServerProps.class);
    }

    @Override
    public void start() {
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
