package com.funny.memes.funnymemes.service.impl;

import com.funny.memes.funnymemes.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.Md5Utils;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static java.util.stream.Collectors.toList;

@Service
public class FileServiceImpl implements FileService {

    private final static Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);

    private static List<String> filesMd5Sums = new ArrayList<>();

    @Autowired
    private S3AsyncClient s3AsyncClient;

    @Value("${app.awsServices.bucketName}")
    private String awsS3BucketName;

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
    public CompletableFuture<String> uploadMediaToS3(String fileName) {
        String localMd5Sum = "";
        try {
            localMd5Sum = BinaryUtils.toHex(Md5Utils.computeMD5Hash(Paths.get(fileName).toFile()));
        } catch (IOException e) {
            LOG.error("Local file {} not found", fileName);
        }

        if (filesMd5Sums.contains(localMd5Sum)) {
            return CompletableFuture.completedFuture("File exist in s3");
        }

        LOG.debug("Start upload file {} to s3", fileName);

        String key = UUID.randomUUID().toString();

        CompletableFuture<PutObjectResponse> future = s3AsyncClient.putObject(
                PutObjectRequest.builder()
                        .bucket(awsS3BucketName)
                        .key(key)
                        .build(),
                AsyncRequestBody.fromFile(Paths.get(fileName))
        );
        future.handle((resp, err) -> {
            if (err != null) {
                LOG.error("Exception in uploadMediaToS3 while put file to s3 bucket: {}", err.getMessage());
            }

            return resp;
        });

        CompletableFuture<String> result = future.thenApply(resp -> {
            String url = "";
            if (resp != null) {
                url = s3AsyncClient.utilities()
                        .getUrl(GetUrlRequest.builder().bucket(awsS3BucketName).key(key).build()).toString();
                LOG.info("Put object url: {}", url);
            }

            return url;
        });
        future.join();

        return result;
    }

    @Override
    public CompletableFuture<String> getAllBucketObjects() {
        ListObjectsRequest listObjects = ListObjectsRequest
                .builder()
                .bucket(awsS3BucketName)
                .build();

        CompletableFuture<ListObjectsResponse> responseFuture = s3AsyncClient.listObjects(listObjects);

        responseFuture.handle((resp, err) -> {
            if (err != null) {
                LOG.error("Exception in getAllBucketObjects while get list of objects");
            }

            return resp;
        });

        CompletableFuture<String> result = responseFuture.thenApply(resp -> {
            List<S3Object> objects = resp.contents();

            filesMd5Sums.clear();

            filesMd5Sums.addAll(objects.stream()
                    .map(s3 -> {
                        String eTag = s3.eTag();
                        if (eTag.contains("\"")) {
                            eTag = eTag.replaceAll("\"", "");
                        }
                        return eTag;
                    })
                    .collect(toList())
            );

//            for (ListIterator iterVals = objects.listIterator(); iterVals.hasNext(); ) {
//                S3Object myValue = (S3Object) iterVals.next();
//                System.out.print("\n The name of the key is " + myValue.key());
//                System.out.print("\n The object is " + myValue.size());
//                System.out.print("\n The owner is " + myValue.owner());
//
//                String eTag = myValue.eTag();
//                if (eTag.contains("\"")) {
//                    eTag = eTag.replaceAll("\"", "");
//                }
//                filesMd5Sums.add(eTag);
//            }

            return "Complete";
        });

        responseFuture.join();

        return result;
    }

    @Override
    public List<String> getFilesMd5Sums() {
        return filesMd5Sums;
    }
}
