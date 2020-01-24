package com.funny.memes.funnymemes.parsers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.funny.memes.funnymemes.entity.Meme;
import com.funny.memes.funnymemes.entity.RedditMemeDeserializer;
import com.funny.memes.funnymemes.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static com.funny.memes.funnymemes.config.Const.FILE_EXIST_IN_REMOTE_STORAGE;
import static java.util.stream.Collectors.toList;

/**
 * Author: Valentin Ershov
 * Date: 14.01.2020
 */
@Component
public class ParseProcessorImpl implements ParseProcessor {

    private final static Logger LOG = LoggerFactory.getLogger(ParseProcessorImpl.class);

    @Value("#{'${reddit.group}'.split(',')}")
    private List<String> propertyRedditGroups;

    @Value("${reddit.postfix}")
    private String redditPostfix;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FileService fileService;

    //    @Override
    private List<Meme> getRedditGroupsContent(String redditGroupName) {
        LOG.debug("ParseProcessor ({}): Start parsing reddit group \"{}\"", Thread.currentThread().getName(), redditGroupName);

        List<Meme> memes = new ArrayList<>();
        final HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "java:com.funny.memes.funnymemes:v1.0.0 (by scuderia1000)");

        HttpEntity<String> httpEntity = new HttpEntity<>(headers);

        ResponseEntity<ObjectNode> response = restTemplate.exchange(
                redditGroupName,
                HttpMethod.GET,
                httpEntity,
                ObjectNode.class
        );
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            LOG.debug(
                    "ParseProcessor ({}): Successfully get data from reddit group \"{}\"",
                    Thread.currentThread().getName(),
                    redditGroupName
            );

            ObjectNode objectNode = response.getBody();
            if (objectNode != null) {
                JsonNode data = objectNode.get("data");
                if (data != null) {
                    JsonNode arrayNode = data.get("children");
                    if (arrayNode != null) {
                        String children = arrayNode.toString();

                        SimpleModule simpleModule = new SimpleModule("RedditMemeDeserializer");
                        simpleModule.addDeserializer(Meme.class, new RedditMemeDeserializer(Meme.class));

                        ObjectMapper objectMapper = new ObjectMapper();
                        objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
                        objectMapper.registerModule(simpleModule);

                        try {
                            memes = objectMapper.readValue(children, new TypeReference<List<Meme>>() {
                            });

                            LOG.debug("ParseProcessor ({}): Parsing reddit group \"{}\" completed", Thread.currentThread().getName(), redditGroupName);

                        } catch (IOException e) {
                            LOG.debug("IOException in ParseProcessor ({}) while parsing reddit group \"{}\"", Thread.currentThread().getName(), redditGroupName);
                            e.printStackTrace();
                        }
                    }
                }
            }
        }

        return memes;
//        return CompletableFuture.completedFuture(memes).exceptionally(ex -> {
//            LOG.debug("Exception in ParseProcessor ({}) while parsing reddit group \"{}\"", Thread.currentThread().getName(), redditGroupName);
//            ex.printStackTrace();
//
//            return new ArrayList<>();
//        });
    }

    private CompletableFuture<List<Meme>> downloadGroupContent(String groupUrl) {
        return CompletableFuture.supplyAsync(() -> getRedditGroupsContent(groupUrl));
    }

    @Async
    @Override
    public void processRedditGroups() throws ExecutionException, InterruptedException {
        for (String groupUrl : propertyRedditGroups) {
            CompletableFuture<List<Meme>> contentFuture = downloadGroupContent(groupUrl + redditPostfix);
            List<Meme> memesList = contentFuture
                    .thenApply(memes -> memes.stream()
                            .filter(Objects::nonNull)
                            .filter(meme -> !StringUtils.isEmpty(meme.getImagePath()))
                            .filter(meme -> {
                                String imagePath = meme.getImagePath();
                                String extension = imagePath.substring(imagePath.lastIndexOf(".") + 1);

                                return "jpg".equals(extension) || "jpeg".equals(extension);
                            })
                            .collect(toList()))
                    .get();
            for (Meme meme : memesList) {
                fileService.downloadImageAsync(meme.getImagePath());
            }
        }
//        CompletableFuture<List<Meme>> memesFuture = propertyRedditGroups.stream()
//                .flatMap(groupName -> downloadGroupContent(groupName + redditPostfix)).collect(toList());
//                            .thenApply(memes -> memes.stream()
//                                    .filter(Objects::nonNull)
//                                    .filter(meme -> !StringUtils.isEmpty(meme.getImagePath()))
//                                    .filter(meme -> {
//                                        String imagePath = meme.getImagePath();
//                                        String extension = imagePath.substring(imagePath.lastIndexOf(".") + 1);
//
//                                        return "jpg".equals(extension) || "jpeg".equals(extension);
//                                    }).collect(toList())
//                            )
//                ).collect(toList());

//                                        .filter(meme -> {
//                                            fileService.downloadImageAsync(meme.getImagePath())
//                                                    .thenApply(fileName -> {
//                                                        if (!StringUtils.isEmpty(fileName)) {
//                                                            fileService.uploadMediaToS3(fileName)
//                                                                    .thenApply(s3Url -> {
//                                                                        if (!StringUtils.isEmpty(s3Url) && !s3Url.equals(FILE_EXIST_IN_REMOTE_STORAGE)) {
//                                                                            meme.setMediaUrl(s3Url);
//                                                                            LOG.debug("Meme s3 url is: {}", meme.getMediaUrl());
//                                                                            try {
//                                                                                Files.deleteIfExists(Paths.get(fileName));
//                                                                            } catch (IOException x) {
//                                                                                LOG.error("Error delete file: {}", fileName);
//                                                                            }
//                                                                            return true;
//                                                                        }
//                                                                        return false;
//                                                                    });
//                                                        }
//                                                        return false;
//                                                    });
//
//                                        })

//                )
//                .collect(toList());

//        List<Meme> memes = propertyRedditGroups.stream()
//                .flatMap(groupName -> getRedditGroupsContent(groupName + redditPostfix).stream())
//                .collect(toList());

//        List<Meme> uniqueMemes = memes.stream()
////                .filter(Objects::nonNull)
////                .filter(meme -> !StringUtils.isEmpty(meme.getImagePath()))
////                .filter(meme -> {
////                    String imagePath = meme.getImagePath();
////                    String extension = imagePath.substring(imagePath.lastIndexOf(".") + 1);
////
////                    return "jpg".equals(extension) || "jpeg".equals(extension);
////                })
//                .filter(meme -> {
//                    String fileName = fileService.downloadImage(meme.getImagePath());
//
//                    if (!StringUtils.isEmpty(fileName)) {
//                        try {
//                            String s3url = fileService.uploadMediaToS3(fileName).get();
//                            if (!StringUtils.isEmpty(s3url) && !s3url.equals(FILE_EXIST_IN_REMOTE_STORAGE)) {
//                                meme.setMediaUrl(s3url);
//                                LOG.debug("Meme s3 url is: {}", meme.getMediaUrl());
//
////                                try {
////                                    Files.deleteIfExists(Paths.get(fileName));
////                                } catch (IOException x) {
////                                    LOG.error("Error delete file: {}", fileName);
////                                }
//
//                                return true;
//                            }
//                        } catch (InterruptedException | ExecutionException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    return false;
//                })
//                .collect(toList());
//
//        for (Meme meme : uniqueMemes) {
//            System.out.println("Meme s3 url: " + meme.getMediaUrl());
//        }
    }

    @Override
    public void processTwitterGroups() {

    }
}
