package org.fh.gae;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;
import lombok.extern.slf4j.Slf4j;
import org.fh.gae.config.GaeServerProps;
import org.fh.gae.net.GaeHttpServer;
import org.fh.gae.query.index.loader.file.IndexIncrementLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

import java.util.concurrent.CountDownLatch;

@Slf4j
public class GaeAppEventListener implements ApplicationListener<ApplicationContextEvent> {
    private Vertx vertx;

    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        // 上下文初始化完成时启动vertx
        if (event instanceof ContextRefreshedEvent) {
            startHttpServer(event.getApplicationContext());

        } else if (event instanceof ContextClosedEvent) {
            // 上下文关闭时关闭vertx
            IndexIncrementLoader loader = event.getApplicationContext().getBean(IndexIncrementLoader.class);

            try {
                vertx.close();
                if (null != loader) {
                    loader.shutdown();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void startHttpServer(ApplicationContext springCtx) {
        GaeServerProps props = springCtx.getBean(GaeServerProps.class);

        GaeHttpServer.springCtx = springCtx;


        // worker线程池选项
        VertxOptions options = new VertxOptions();
        int nioThreads = props.getNioThread() > 0 ? props.getNioThread() : Runtime.getRuntime().availableProcessors();
        options.setEventLoopPoolSize(nioThreads);
        options.setMaxWorkerExecuteTime(1000);
        // options.setWorkerPoolSize(nioThreads * 2);

        // 部署选项
        Vertx vertx = Vertx.vertx(options);
        DeploymentOptions depOptions = new DeploymentOptions();
        // 设置verticle数量为NIO线程数的2倍
        int verticleCount = nioThreads > 0 ? nioThreads * 2 : Runtime.getRuntime().availableProcessors();
        depOptions.setInstances(verticleCount);

        // 部署
        CountDownLatch latch = new CountDownLatch(1);
        vertx.deployVerticle(GaeHttpServer.class.getName(), depOptions, ar -> {
            if (ar.failed()) {
                log.warn("deploy failed, msg:", ar.cause());
            }

            latch.countDown();
        });

        try {
            // 等待部署完成
            latch.await();
            log.info("deploy succeeded, nioThread = {}, verticles = {}", nioThreads, verticleCount);

        } catch (InterruptedException e) {
            log.error("", e);
        }

        this.vertx = vertx;
    }
}
