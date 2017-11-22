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

    private volatile boolean shutdown = false;

    private int fileIndex = 0;

    /**
     * 当前增量文件名
     */
    private String currentFileName;

    /**
     * 下一个增量文件名
     */
    private String nextFileName;

    /**
     * 上次加载完成后增量文件指针位置
     */
    private long lastSeek = 0;

    @PostConstruct
    public void startWatch() {
        log.info("file index applied");

        currentFileName = indexProps.getIncrPath() + File.separator + indexProps.getIncrName() + "." + fileIndex;
        nextFileName = genNextFileName();

        new Thread(this).start();
    }

    @Override
    public void run() {
        int interval = indexProps.getIncrInterval();

        while (shutdown) {
            File file = new File(currentFileName.toString());
            if (false == file.exists()) {
                try {
                    Thread.sleep(interval);
                    continue;

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            loadIncrement(file);

            // 尝试读取下一个增量文件
            File nextFile = tryNextFile();
            if (null != nextFile) {
                log.info("loading new index file:{}", currentFileName);
                loadIncrement(file);
            }
        }
    }

    public void shutdown() {
        this.shutdown = true;
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

    private File tryNextFile() {
        File f = new File(nextFileName);
        if (!f.exists()) {
            return null;
        }

        // 重置文件名
        currentFileName = nextFileName;
        ++fileIndex;
        nextFileName = genNextFileName();

        // 重置文件指针位置
        lastSeek = 0;

        return f;
    }

    private String genNextFileName() {
        return indexProps.getIncrPath() + File.separator + indexProps.getIncrName() + "." + (fileIndex + 1);
    }
}
