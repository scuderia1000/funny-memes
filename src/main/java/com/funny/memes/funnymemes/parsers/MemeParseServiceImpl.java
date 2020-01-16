package com.funny.memes.funnymemes.parsers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.util.List;

/**
 * Author: Valentin Ershov
 * Date: 14.01.2020
 */
@Service
public class MemeParseServiceImpl implements MemeParseService {

    private final static Logger LOG = LoggerFactory.getLogger(MemeParseServiceImpl.class);

    @Value("#{'${reddit.group}'.split(',')}")
    private List<String> propertyRedditGroups;

    @Value("${parse-meme-thread.wait.time}")
    private Long propertyThreadWaitTime;

    @Value("${reddit.postfix}")
    private String redditPostfix;

    private volatile boolean canRestart = true;

    private final static Object lock = new Object();

    private ThreadWatcher threadWatcher;

    @Autowired
    private ParseProcessor parseProcessor;

    @Async
    @Override
    public void initialize() {
        threadWatcher = new ThreadWatcher(propertyThreadWaitTime, this);
        threadWatcher.start();
        restartParseProcessing();
    }

    @Async
    @Override
    public void restartParseProcessing() {
        LOG.debug("Parse service ({}): Received call to restart parse process", Thread.currentThread().getName());

        synchronized (lock) {
            if (!canRestart) {
                LOG.debug("Parse service ({}): Can not restart parse process, another restart process already running", Thread.currentThread().getName());
                return;
            }

            LOG.debug("Parse service ({}): Restarting parse process", Thread.currentThread().getName());
            canRestart = false;
        }
        for (String groupName : propertyRedditGroups) {
            LOG.info("Start process reddit group name: {}", groupName);

            StringBuilder redditGroupUrl = new StringBuilder(groupName);
            redditGroupUrl.append(redditPostfix);
            parseProcessor.startParseProcessing(redditGroupUrl.toString());
        }
        LOG.debug("Parse service ({}): Parse process restarted", Thread.currentThread().getName());
//        synchronized (lock){
//            canRestart = true;
//        }
    }

    @PreDestroy
    public void destroy() {
        threadWatcher.shutdown();
    }
}
