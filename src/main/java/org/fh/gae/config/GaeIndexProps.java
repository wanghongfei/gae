package org.fh.gae.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gae.index.file")
@Data
@NoArgsConstructor
public class GaeIndexProps {
    /**
     * 全量索引文件所在目录
     */
    private String path = "index";

    /**
     * 全量索引文件名
     */
    private String name = "gae.idx";

    /**
     * 增量索引所在目录
     */
    private String incrPath = "index";

    /**
     * 增量索引文件名
     */
    private String incrName = "gae.idx.incr";

    /**
     * 增量索引更新间隔, 毫秒
     */
    private Integer incrInterval = 500;
}
