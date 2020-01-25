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
import com.funny.memes.funnymemes.service.MemeService;
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
import java.util.concurrent.*;

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

    @Autowired
    private MemeService memeService;

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

        return memes.stream()
                .filter(Objects::nonNull)
                .filter(meme -> !StringUtils.isEmpty(meme.getImagePath()))
                .filter(meme -> {
                    String imagePath = meme.getImagePath();
                    String extension = imagePath.substring(imagePath.lastIndexOf(".") + 1);

                    return "jpg".equals(extension) || "jpeg".equals(extension);
                })
                .collect(toList());
    }

    private CompletableFuture<List<Meme>> downloadGroupContent(String groupUrl) {
        return CompletableFuture.supplyAsync(() -> getRedditGroupsContent(groupUrl));
    }

    private String getS3MediaUrl(Meme meme) {
        String result = null;
        String fileName = fileService.downloadImage(meme.getImagePath());
        if (!StringUtils.isEmpty(fileName)) {
            try {
                String s3url = fileService.uploadMediaToS3(fileName).get();
                if (!StringUtils.isEmpty(s3url) && !s3url.equals(FILE_EXIST_IN_REMOTE_STORAGE)) {
                    result = s3url;
                }

                try {
                    Files.deleteIfExists(Paths.get(fileName));
                } catch (IOException x) {
                    LOG.error("Error delete file: {}", fileName);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        return result;
    }

    private CompletableFuture<List<Meme>> uploadGroupContents(List<Meme> memes) {
        return CompletableFuture.supplyAsync(() -> memes.parallelStream()
                .map(meme -> {
                    String s3Url = getS3MediaUrl(meme);
                    if (!StringUtils.isEmpty(s3Url)) {
                        meme.setMediaUrl(s3Url);
                        LOG.debug("Meme s3 url is: {}", meme.getMediaUrl());
                    }
                    return meme;
                })
                .filter(meme -> !StringUtils.isEmpty(meme.getMediaUrl()))
                .collect(toList()));
    }

    @Async
    @Override
    public CompletableFuture<List<Meme>> processRedditGroups() throws ExecutionException, InterruptedException {
        List<Meme> result = new ArrayList<>();
        for (String groupUrl : propertyRedditGroups) {
            CompletableFuture<List<Meme>> memesFuture = downloadGroupContent(groupUrl + redditPostfix)
                    .thenCompose(this::uploadGroupContents);
            result.addAll(memeService.saveMemes(memesFuture.get()));
        }

        return CompletableFuture.completedFuture(result);
    }

    @Override
    public void processTwitterGroups() {

    }
}
