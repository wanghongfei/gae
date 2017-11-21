package org.fh.gae.query.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BenchTimer {
    private long start;

    public BenchTimer() {
        this.start = System.currentTimeMillis();
    }

    public void recordTime(String flag) {
        long end = System.currentTimeMillis();
        log.info("{}: {}", flag, end - start);

        this.start = end;
    }
}
