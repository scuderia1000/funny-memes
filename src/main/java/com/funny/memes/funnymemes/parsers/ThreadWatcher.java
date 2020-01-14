package com.funny.memes.funnymemes.parsers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: Valentin Ershov
 * Date: 14.01.2020
 */
public class ThreadWatcher extends Thread {

    private final static Logger LOG = LoggerFactory.getLogger(ThreadWatcher.class);

    private long threadWaitTime;

    private volatile boolean isRunning = true;

    private MemeParseService parseService;

    public ThreadWatcher(long threadWaitTime, MemeParseService parseService) {
        this.threadWaitTime = threadWaitTime;
        this.parseService = parseService;
        setName("ParseThreadWatcher");
    }

    @Override
    public void run() {
        LOG.debug("ThreadWatcher ({}): Starting thread watcher with thread wait time = {}", Thread.currentThread().getName(), threadWaitTime);

        while (isRunning) {
            if (Thread.currentThread().isInterrupted()) {
                LOG.debug("ThreadWatcher ({}): Thread watcher interrupted", Thread.currentThread().getName());
                return;
            }

            LOG.debug("ThreadWatcher ({}): Thread watcher is restarting the parse service", Thread.currentThread().getName());
            parseService.restartParseProcessing();
            try {
                Thread.sleep(threadWaitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
                shutdown();
            }
        }
        LOG.debug("ThreadWatcher ({}): Thread watcher stopped", Thread.currentThread().getName());
    }

    void shutdown() {
        LOG.debug("ThreadWatcher ({}): Stopping thread watcher", Thread.currentThread().getName());
        this.isRunning = false;
        this.interrupt();
    }
}
