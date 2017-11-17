package org.fh.gae.config;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "gae.dict")
@Data
@NoArgsConstructor
public class GaeDictProps {
    /**
     * IP字典文件名
     */
    private String ip = "dict/ipdict.txt";
}
