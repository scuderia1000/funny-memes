package com.funny.memes.funnymemes.service.impl;

import com.funny.memes.funnymemes.parsers.ParseProcessorImpl;
import com.funny.memes.funnymemes.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetUrlRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.util.concurrent.CompletableFuture;

@Service
public class FileServiceImpl implements FileService {

    private final static Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);

    @Autowired
    private S3AsyncClient s3AsyncClient;

    @Value("${app.awsServices.bucketName}")
    private String amazonBucketName;

    @Override
    public String downloadImage(String url) {
        LOG.debug("Start file downloading from url: {}", url);
        String fileName = url.substring(url.lastIndexOf("/") + 1);
        String extension = fileName.substring(fileName.lastIndexOf(".") + 1);

        if ("jpg".equals(extension) || "jpeg".equals(extension)) {
            try {
                try (InputStream inputStream = new URL(url).openStream()) {
                    try (ReadableByteChannel readChannel = Channels.newChannel(inputStream)) {
                        try (FileOutputStream fileOS = new FileOutputStream(fileName)) {
                            try (FileChannel writeChannel = fileOS.getChannel()) {
                                writeChannel.transferFrom(readChannel, 0, Long.MAX_VALUE);
                            }
                        }
                    }
                }
            } catch (IOException ex) {
                LOG.debug("Error downloading file from url: {}", url);
                ex.printStackTrace();
                return null;
            }
        }
        LOG.debug("Complete file downloading from url: {}", url);
        return fileName;
    }

    @Override
    public String uploadMediaToS3(String fileName) {
        LOG.debug("Start upload file {} to s3", fileName);

        String key = fileName;
        CompletableFuture<PutObjectResponse> future = s3AsyncClient.putObject(
                PutObjectRequest.builder()
                        .bucket(amazonBucketName)
                        .key(key)
                        .build(),
                AsyncRequestBody.fromFile(Paths.get(fileName))
        );
        future.whenComplete((resp, err) -> {
            try {
                if (resp != null) {
                    LOG.info("Put object response: {}", resp);
                } else {
                    // Handle error
                    err.printStackTrace();
                }
            } finally {
                // Lets the application shut down. Only close the client when you are completely done with it.
                s3AsyncClient.close();
            }
        }).thenApply(resp -> {
            final URL reportUrl = s3AsyncClient.utilities()
                    .getUrl(GetUrlRequest.builder().bucket(amazonBucketName).key(key).build());
            LOG.info("Put object url: {}", reportUrl.toString());

            return reportUrl.toString();
        });

        future.join();

        return "test";
    }
}
