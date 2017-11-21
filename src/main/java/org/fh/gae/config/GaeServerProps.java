package org.fh.gae.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gae.server")
@Data
@NoArgsConstructor
public class GaeServerProps {
    /**
     * 监听端口
     */
    private int port = 8080;

    /**
     * 绑定的主机名
     */
    private String host = "127.0.0.1";

    /**
     * 业务线程池大小
     */
    private int businessThreadPoolSize = 5;

    private String searchLog = "logs/search.log";

    /**
     * 业务线程池最大线程数;
     * 如果没有IO操作, 最好与CPU核心数相同
     */
    private int maxBizThread = 4;

    /**
     * 业务线程池最小线程数
     */
    private int minBizThread = 2;
}
