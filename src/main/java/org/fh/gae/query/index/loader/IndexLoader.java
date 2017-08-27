package org.fh.gae.query.index.loader;

import lombok.extern.slf4j.Slf4j;
import org.fh.gae.query.index.GaeIndex;
import org.fh.gae.query.index.auth.AuthIndex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

/**
 * 全量索引加载器
 */
@Component
@Slf4j
public class IndexLoader {
    private static final String TOKEN_SEPARATOR = "\t";

    @Autowired
    private AuthIndex authIndex;

    @Value("${gae.index.path}")
    private String idxPath;

    @Value("${gae.index.name}")
    private String idxName;

    private Map<Integer, GaeIndex> idxMap;

    @PostConstruct
    public void init() throws IOException {
        idxMap = new HashMap<>();
        idxMap.put(AuthIndex.LEVEL, authIndex);

        loadIndex();
    }

    public void loadIndex() throws IOException {
        try (FileInputStream fis = new FileInputStream(idxPath + File.separator + idxName + ".0")) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(fis)
            );

            String line;
            while ( (line = reader.readLine()) != null ) {
                log.info("loading: {}", line);
                processLine(line);
            }

        } catch (IOException e) {
            throw e;
        }
    }

    private void processLine(String line) {
        try {
            String[] tokens = line.split(TOKEN_SEPARATOR);

            // 取出层级
            Integer level = Integer.valueOf(tokens[0]);
            GaeIndex index = idxMap.get(level);

            if (tokens.length != index.getLength() + 2) {
                log.error("invalid index line: {}", line);
                return;
            }

            // 取出key
            String key = tokens[2];

            Object info = index.packageInfo(tokens);
            switch (level) {
                case 0:
                    index.add(key, info);
                    break;
                case 1:
                    index.update(key, info);
                    break;
                case 2:
                    index.delete(key, info);
                    break;

                default:
                    log.error("invalid index line: {}", line);
                    return;
            }

        } catch (Exception e) {
            log.error("invalid index line: {}", line);
        }

    }
}
