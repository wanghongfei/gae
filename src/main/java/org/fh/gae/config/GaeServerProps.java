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
}
