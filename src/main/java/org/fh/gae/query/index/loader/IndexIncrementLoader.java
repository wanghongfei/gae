package org.fh.gae.query.index.loader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 增量索引加载器
 */
@Component
@Slf4j
public class IndexIncrementLoader implements Runnable {
    @Autowired
    private IndexLoader indexLoader;

    @Value("${gae.index.incr-path}")
    private String idxPath;

    @Value("${gae.index.incr-name}")
    private String idxName;

    @Value("${gae.index.incr-interval}")
    private long interval = 500;

    /**
     * 上次加载完成后增量文件指针位置
     */
    private long lastSeek = 0;

    @PostConstruct
    public void startWatch() {
        new Thread(this).start();
    }

    @Override
    public void run() {
        String filename = idxPath + File.separator + idxName;

        while (true) {
            File file = new File(filename);
            if (false == file.exists()) {
                try {
                    Thread.sleep(interval);
                    continue;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            loadIncrement(file);
        }
    }

    private void loadIncrement(File file) {
        try (RandomAccessFile randomFile = new RandomAccessFile(file, "r")) {
            randomFile.seek(lastSeek);

            String line;
            while ( (line = randomFile.readLine()) != null ) {
                log.info("index: {}", line);
                indexLoader.processLine(line);

                lastSeek = randomFile.getFilePointer();
            }


        } catch (IOException e) {
            log.error(e.getMessage());
        }

    }
}
