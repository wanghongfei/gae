package org.fh.gae.query.index.loader;

import lombok.extern.slf4j.Slf4j;
import org.fh.gae.query.index.GaeIndex;
import org.fh.gae.query.index.auth.AuthIndex;
import org.fh.gae.query.index.plan.PlanIndex;
import org.fh.gae.query.index.plan.PlanInfo;
import org.fh.gae.query.index.unit.AdUnitIndex;
import org.fh.gae.query.index.user.UserIndex;
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
    @Autowired
    private UserIndex userIndex;
    @Autowired
    private PlanIndex planIndex;
    @Autowired
    private AdUnitIndex unitIndex;

    @Value("${gae.index.path}")
    private String idxPath;

    @Value("${gae.index.name}")
    private String idxName;

    private Map<Integer, GaeIndex> idxMap;

    @PostConstruct
    public void init() throws IOException {
        idxMap = new HashMap<>();
        idxMap.put(AuthIndex.LEVEL, authIndex);
        idxMap.put(UserIndex.LEVEL, userIndex);
        idxMap.put(PlanIndex.LEVEL, planIndex);
        idxMap.put(AdUnitIndex.LEVEL, unitIndex);

        loadIndex();
    }

    private void loadIndex() throws IOException {
        try (FileInputStream fis = new FileInputStream(idxPath + File.separator + idxName + ".0")) {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(fis)
            );

            String line;
            while ( (line = reader.readLine()) != null ) {
                processLine(line);
            }

        } catch (IOException e) {
            throw e;
        }
    }

    public void processLine(String line) {
        try {
            if (0 == line.length()) {
                log.info("skip empty line");
                return;
            }
            
            log.info("{}", line);

            String[] tokens = line.split(TOKEN_SEPARATOR);

            // 取出操作类型
            Integer op = Integer.valueOf(tokens[1]);
            // 取出层级
            Integer level = Integer.valueOf(tokens[0]);
            GaeIndex index = idxMap.get(level);

            if (tokens.length != index.getLength() + 2) {
                log.error("invalid index line: {}", line);
                return;
            }

            Object info = index.packageInfo(tokens);
            switch (op) {
                case 0:
                    index.add(info);
                    break;
                case 1:
                    index.update(info);
                    break;
                case 2:
                    index.delete(info);
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
