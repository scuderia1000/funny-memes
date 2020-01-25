package com.funny.memes.funnymemes.parsers;

import com.funny.memes.funnymemes.entity.Meme;
import com.funny.memes.funnymemes.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.funny.memes.funnymemes.config.Const.ERROR;
import static com.funny.memes.funnymemes.config.Const.FILE_EXIST_IN_REMOTE_STORAGE;
import static java.util.stream.Collectors.toList;

/**
 * Author: Valentin Ershov
 * Date: 14.01.2020
 */
@Service
public class MemeParseServiceImpl implements MemeParseService {

    private final static Logger LOG = LoggerFactory.getLogger(MemeParseServiceImpl.class);

//    @Value("#{'${reddit.group}'.split(',')}")
//    private List<String> propertyRedditGroups;

    @Value("${parse-meme-thread.wait.time}")
    private Long propertyThreadWaitTime;

//    @Value("${reddit.postfix}")
//    private String redditPostfix;

    private volatile boolean canRestart = true;

    private final static Object lock = new Object();

    private ThreadWatcher threadWatcher;

    @Autowired
    private ParseProcessor parseProcessor;

    @Autowired
    private FileService fileService;

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

        String remoteStorageMd5Sums = "";
        try {
            remoteStorageMd5Sums = fileService.getAllBucketObjects().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (!remoteStorageMd5Sums.equals(ERROR)) {
            try {
                CompletableFuture<List<Meme>> redditResult = parseProcessor.processRedditGroups();
                // TODO сделать так, чтобы поток не ждал этот результат
                for (Meme meme : redditResult.get()) {
                    System.out.println("Saved Reddit meme" + meme.toString());
                }
//                parseProcessor.processTwitterGroups();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

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
