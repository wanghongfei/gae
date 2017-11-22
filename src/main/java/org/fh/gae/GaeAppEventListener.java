package org.fh.gae;

import lombok.extern.slf4j.Slf4j;
import org.fh.gae.net.GaeHttpServer;
import org.fh.gae.net.ThreadPool;
import org.fh.gae.query.index.loader.file.IndexIncrementLoader;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.ContextRefreshedEvent;

@Slf4j
public class GaeAppEventListener implements ApplicationListener<ApplicationContextEvent> {
    @Override
    public void onApplicationEvent(ApplicationContextEvent event) {
        // 上下文初始化完成时启动netty server
        if (event instanceof ContextRefreshedEvent) {
            GaeHttpServer server = event.getApplicationContext().getBean(GaeHttpServer.class);

            try {
                server.start();

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (event instanceof ContextClosedEvent) {
            // 上下文关闭时关闭netty server
            GaeHttpServer server = event.getApplicationContext().getBean(GaeHttpServer.class);
            ThreadPool pool = event.getApplicationContext().getBean(ThreadPool.class);

            IndexIncrementLoader loader = event.getApplicationContext().getBean(IndexIncrementLoader.class);

            try {
                server.shutdown();
                pool.shudown(true);
                if (null != loader) {
                    loader.shutdown();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }
}
