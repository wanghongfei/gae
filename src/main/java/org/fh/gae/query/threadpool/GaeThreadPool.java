package org.fh.gae.query.threadpool;

import org.fh.gae.config.GaeThreadPoolProps;
import org.fh.gae.query.session.ThreadCtx;
import org.fh.gae.query.vo.Ad;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@ConditionalOnProperty(prefix = "gae.thread-pool", name = "enable", havingValue = "true", matchIfMissing = false)
public class GaeThreadPool {
    @Autowired
    private GaeThreadPoolProps poolProps;

    private ThreadPoolExecutor bidThreadPool;

    /**
     * 提交广告位检索任务
     *
     * @param callable
     * @param propagateThreadLocal 是否传递ThreadLocal
     *
     * @return
     */
    public Future<Ad> submitBidTask(Callable<Ad> callable, boolean propagateThreadLocal) {
        Map<String, Object> ctx = ThreadCtx.getOriginContext();

        return bidThreadPool.submit(() -> {
            if (propagateThreadLocal) {
                ThreadCtx.putOriginContext(ctx);
            }

            Ad ad = callable.call();
            ThreadCtx.clean();

            return ad;
        });
    }

    @PostConstruct
    private void initPool() {
        bidThreadPool = new ThreadPoolExecutor(
                poolProps.getCoreSize(),
                poolProps.getMaxSize(),
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(poolProps.getQueueSize()),
                new NamingThreadFactory("bid-pool"),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }


    private class NamingThreadFactory implements ThreadFactory {
        private String namePrefix;

        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        public NamingThreadFactory(String prefix) {
            SecurityManager s = System.getSecurityManager();
            group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();

            namePrefix = prefix + "-thread-";
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(
                    group,
                    r,
                    namePrefix + threadNumber.getAndIncrement(),
                    0
            );

            if (t.isDaemon()) {
                t.setDaemon(false);
            }

            if (t.getPriority() != Thread.NORM_PRIORITY) {
                t.setPriority(Thread.NORM_PRIORITY);
            }

            return t;
        }

    }
}
