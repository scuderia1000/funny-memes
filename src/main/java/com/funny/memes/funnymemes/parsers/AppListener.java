package com.funny.memes.funnymemes.parsers;

import com.funny.memes.funnymemes.dao.MemeRepository;
import com.funny.memes.funnymemes.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@Component
public class AppListener implements ApplicationListener<ContextRefreshedEvent> {

    private final static Logger LOG = LoggerFactory.getLogger(AppListener.class);

    private final static Object lock = new Object();

    private volatile boolean alreadyStarted = false;

    @Autowired
    private MemeParseService memeParseService;

    @Autowired
    private MemeRepository repository;

    @Autowired
    private FileService fileService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        synchronized (lock) {
            if (alreadyStarted) return;
            alreadyStarted = true;
        }

        LOG.debug("Deleting all memes from repository");
        repository.deleteAll();
        LOG.debug("Deleting all memes from repository complete");

        fileService.deleteAllBucketObjects();

        memeParseService.initialize();
    }
}
