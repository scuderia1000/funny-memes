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

import static com.funny.memes.funnymemes.config.Const.FILE_EXIST_IN_REMOTE_STORAGE;
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

        try {
            fileService.getAllBucketObjects().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        if (!fileService.getFilesMd5Sums().isEmpty()) {

        }

        List<CompletableFuture<List<Meme>>> features = propertyRedditGroups.stream()
                .map(groupName -> parseProcessor.startParseProcessing(groupName + redditPostfix))
                .collect(toList());

        CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                features.toArray(new CompletableFuture[0])
        );

        CompletableFuture<List<Meme>> memesFuture = allFutures
                .thenApply(justVoid -> features.stream()
                        .flatMap(feature -> feature.join().stream())
                        .collect(toList())
                );
        List<Meme> memes = new ArrayList<>();
        try {
            memes = memesFuture.get();
            memes.remove(null);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        memes = memes.stream()
                .filter(Objects::nonNull)
                .filter(meme -> !StringUtils.isEmpty(meme.getImagePath()))
                .filter(meme -> {
                    String imagePath = meme.getImagePath();
                    String extension = imagePath.substring(imagePath.lastIndexOf(".") + 1);

                    return "jpg".equals(extension) || "jpeg".equals(extension);
                })
                .filter(meme -> {
                    String fileName = fileService.downloadImage(meme.getImagePath());

                    if (!StringUtils.isEmpty(fileName)) {
                        try {
                            String s3url = fileService.uploadMediaToS3(fileName).get();
                            if (!StringUtils.isEmpty(s3url) && !s3url.equals(FILE_EXIST_IN_REMOTE_STORAGE)) {
                                meme.setMediaUrl(s3url);
                                LOG.debug("Meme s3 url is: {}", meme.getMediaUrl());

                                return true;
                            }
                        } catch (InterruptedException | ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                    return false;
                }).collect(toList());

        for (Meme meme : memes) {
            System.out.println("Meme s3 url: " + meme.getMediaUrl());
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
