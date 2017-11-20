package org.fh.gae.query.index.loader.file;

import lombok.extern.slf4j.Slf4j;
import org.fh.gae.config.GaeIndexProps;
import org.fh.gae.query.index.loader.IndexLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * 增量索引加载器
 */
@Component
@ConditionalOnProperty(prefix = "gae.index.file", name = "enable", matchIfMissing = true, havingValue = "true")
@Slf4j
public class IndexIncrementLoader implements Runnable {
    @Autowired
    private IndexLoader indexLoader;

    @Autowired
    private GaeIndexProps indexProps;

    /**
     * 上次加载完成后增量文件指针位置
     */
    private long lastSeek = 0;

    @PostConstruct
    public void startWatch() {
        log.info("file index applied");
        new Thread(this).start();
    }

    @Override
    public void run() {
        String filename = indexProps.getIncrPath() + File.separator + indexProps.getIncrName();
        int interval = indexProps.getIncrInterval();

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
