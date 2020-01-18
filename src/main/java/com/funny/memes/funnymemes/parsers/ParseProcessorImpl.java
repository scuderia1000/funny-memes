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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Author: Valentin Ershov
 * Date: 14.01.2020
 */
@Component
public class ParseProcessorImpl implements ParseProcessor {

    private final static Logger LOG = LoggerFactory.getLogger(ParseProcessorImpl.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private FileService fileService;

    @Async
    @Override
    public CompletableFuture<List<Meme>> startParseProcessing(String redditGroupName) {
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

//                                List<Meme> memes = objectMapper.readValue(children, new TypeReference<List<Meme>>() {});
//                                if (memes != null && !memes.isEmpty()) {
//                                    memes.remove(null);
//                                    for (Meme meme : memes) {
//                                        String imagePath = meme.getImagePath();
//                                        if (!StringUtils.isEmpty(imagePath)) {
//                                            if (imagePath.contains("\"")) {
//                                                imagePath = imagePath.replaceAll("\"", "");
//                                            }
//                                            String extension = imagePath.substring(imagePath.lastIndexOf(".") + 1);
//                                            if ("jpg".equals(extension) || "jpeg".equals(extension)) {
//                                                String fileName = fileService.downloadImage(imagePath);
//                                                if (!StringUtils.isEmpty(fileName)) {
//                                                    String s3Url = fileService.uploadMediaToS3(fileName);
//                                                }
//                                            }
//                                        }
//                                    }
//                                }
                        } catch (IOException e) {
                            LOG.debug("IOException in ParseProcessor ({}) while parsing reddit group \"{}\"", Thread.currentThread().getName(), redditGroupName);
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
        return CompletableFuture.completedFuture(memes).exceptionally(ex -> {
            LOG.debug("Exception in ParseProcessor ({}) while parsing reddit group \"{}\"", Thread.currentThread().getName(), redditGroupName);
            ex.printStackTrace();

            return new ArrayList<>();
        });
    }
}
