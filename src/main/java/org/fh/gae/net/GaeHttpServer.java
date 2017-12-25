package org.fh.gae.net;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.slf4j.Slf4j;
import org.fh.gae.config.GaeServerProps;
import org.fh.gae.net.handler.GaeAuthHandlerVertx;
import org.fh.gae.net.handler.GaeBidHandlerVertx;
import org.fh.gae.net.handler.GaeErrorHandlerVertx;
import org.fh.gae.net.handler.GaeJsonHandlerVertx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GaeHttpServer {

    @Autowired
    private GaeJsonHandlerVertx jsonHandlerVertx;

    @Autowired
    private GaeAuthHandlerVertx authHandlerVertx;

    @Autowired
    private GaeBidHandlerVertx bidHandlerVertx;

    @Autowired
    private GaeErrorHandlerVertx errorHandlerVertx;

    @Autowired
    private GaeServerProps serverProps;

    public void start() throws Exception {
        log.info("starting GAE server");

        Vertx vertx = Vertx.vertx();
        HttpServer server = vertx.createHttpServer();

        Router router = Router.router(vertx);
        router.route().handler(BodyHandler.create());
        router.route("/")
                .handler(jsonHandlerVertx)
                .handler(authHandlerVertx)
                .handler(bidHandlerVertx)
                .failureHandler(errorHandlerVertx);

        server.requestHandler(router::accept).listen(serverProps.getPort());

    }

    public void shutdown() throws Exception {

        log.info("GAE server has been stopped");
    }
}
