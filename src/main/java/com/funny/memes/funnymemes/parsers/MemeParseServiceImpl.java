package com.funny.memes.funnymemes.parsers;

import com.funny.memes.funnymemes.entity.Meme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.PreDestroy;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static java.util.stream.Collectors.toList;

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
        List<CompletableFuture> parseProcesList = new ArrayList<>();
//        CompletableFuture<List<Meme>> [] features = (CompletableFuture<List<Meme>> []) Array.newInstance(CompletableFuture.class, propertyRedditGroups.size());
        List<CompletableFuture<List<Meme>>> features = new ArrayList<>();
//        CompletableFuture<?> [] features = new CompletableFuture<?>[propertyRedditGroups.size()];
        int i = 0;
        for (String groupName : propertyRedditGroups) {
            LOG.info("Start process reddit group name: {}", groupName);

            String redditGroupUrl = groupName + redditPostfix;
            features.add(parseProcessor.startParseProcessing(redditGroupUrl));
//            features[i] = parseProcessor.startParseProcessing(redditGroupUrl);
            i++;
//            parseProcesList.add(parseProcessor.startParseProcessing(redditGroupUrl.toString()));
//            CompletableFuture<List<Meme>> page1 = gitHubLookupService.findUser("PivotalSoftware");
//            parseProcessor.startParseProcessing(redditGroupUrl.toString());
        }
//        parseProcesList.toArray()
        CompletableFuture.allOf(features.toArray(new CompletableFuture<?>[0]))
                .thenAccept(justVoid -> {
                    final List<Meme> memes = features.stream()
                            .flatMap(completableFuture -> completableFuture.join().stream())
                            .collect(toList());
                    for (Meme meme : memes) {
                        System.out.println(meme);
                    }
                });
//        CompletableFuture.allOf(features).join();
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
