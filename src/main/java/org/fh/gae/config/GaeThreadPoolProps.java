package org.fh.gae.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gae.thread-pool")
@Data
@NoArgsConstructor
public class GaeThreadPoolProps {
    /**
     * 线程池core线程数量
     */
    private Integer coreSize = 4;

    /**
     * 线程池最大线程数
     */
    private Integer maxSize = 8;

    /**
     * 任务等待队列最大长度
     */
    private Integer queueSize = 1000;
}
