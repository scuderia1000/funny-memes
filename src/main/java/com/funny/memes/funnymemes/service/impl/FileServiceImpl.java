package com.funny.memes.funnymemes.service.impl;

import com.funny.memes.funnymemes.dto.PutObjectResponseDto;
import com.funny.memes.funnymemes.entity.Meme;
import com.funny.memes.funnymemes.service.FileService;
import com.funny.memes.funnymemes.service.MemeListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import software.amazon.awssdk.core.async.AsyncRequestBody;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.utils.BinaryUtils;
import software.amazon.awssdk.utils.Md5Utils;

import java.io.*;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

import static com.funny.memes.funnymemes.config.Const.*;
import static java.util.stream.Collectors.toList;

@Service
public class FileServiceImpl implements FileService {

    private final static Logger LOG = LoggerFactory.getLogger(FileServiceImpl.class);

    private static Set<String> filesMd5Sums = new HashSet<>();

    @Autowired
    private S3AsyncClient s3AsyncClient;

    @Autowired
    private S3Client s3Client;

    @Autowired
    private MemeListService memeListService;

    @Value("${app.awsServices.bucketName}")
    private String awsS3BucketName;

    @Override
    public CompletableFuture<String> downloadImageAsync(String url) {
        return CompletableFuture.supplyAsync(() -> downloadImage(url));
    }

    @Override
    public String downloadImage(String url) {
        LOG.debug("File Service ({}): Start file downloading from url: {}", Thread.currentThread().getName(), url);
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
                LOG.debug("File Service ({}): Error downloading file from url: {}", Thread.currentThread().getName(), url);
                ex.printStackTrace();
                return null;
            }
        }
        LOG.debug("File Service ({}): Complete file downloading from url: {}", Thread.currentThread().getName(), url);
        return fileName;
    }

    @Override
    public CompletableFuture<PutObjectResponseDto> uploadMediaToS3(String fileName, String lang) {
        String localMd5Sum = "";
        try {
            localMd5Sum = BinaryUtils.toHex(Md5Utils.computeMD5Hash(Paths.get(fileName).toFile()));
        } catch (IOException e) {
            LOG.error("File Service ({}): Local file {} not found", Thread.currentThread().getName(), fileName);
        }

        if (filesMd5Sums.contains(localMd5Sum)) {
            LOG.error("File Service ({}): File {} exist in s3", Thread.currentThread().getName(), fileName);

            return CompletableFuture.completedFuture(new PutObjectResponseDto(FILE_EXIST_IN_REMOTE_STORAGE));
        }

        LOG.debug("File Service ({}): Start upload file {} to s3", Thread.currentThread().getName(), fileName);

        String key = lang + "/" + fileName;

        CompletableFuture<PutObjectResponse> future = s3AsyncClient.putObject(
                PutObjectRequest.builder()
                        .bucket(awsS3BucketName)
                        .key(key)
                        .build(),
                AsyncRequestBody.fromFile(Paths.get(fileName))
        );
        future.handle((resp, err) -> {
            if (err != null) {
                LOG.error("File Service ({}): Exception in uploadMediaToS3 while put file to s3 bucket: {}", Thread.currentThread().getName(), err.getMessage());
            }

            return resp;
        });

        CompletableFuture<PutObjectResponseDto> result = future.thenApply(resp -> {
            PutObjectResponseDto responseDto = new PutObjectResponseDto(ERROR);
            if (resp != null) {
                String url = s3AsyncClient.utilities()
                        .getUrl(GetUrlRequest.builder().bucket(awsS3BucketName).key(key).build()).toString();

                responseDto = new PutObjectResponseDto(OK, resp.eTag(), url);

                LOG.info("File Service ({}): Complete upload file {} to s3, file url: {}", Thread.currentThread().getName(), key, url);
            }

            return responseDto;
        });
        future.join();

        return result;
    }

    @Override
    public CompletableFuture<String> getAllBucketObjectsAsync() {
        ListObjectsRequest listObjects = ListObjectsRequest
                .builder()
                .bucket(awsS3BucketName)
                .build();

        CompletableFuture<ListObjectsResponse> responseFuture = s3AsyncClient.listObjects(listObjects);
        responseFuture.handle((resp, err) -> {
            if (err != null) {
                LOG.error("Exception in getAllBucketObjectsAsync while get list of objects");
            }

            return resp;
        });

        CompletableFuture<String> result = responseFuture.thenApply(resp -> {
            if (resp == null) {
                return ERROR;
            }

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

            return "Complete";
        });

        responseFuture.join();

        return result;
    }

    @Override
    public String updateMd5SumList() {
        List<String> md5Sums = memeListService.getAllMd5Sum();
        filesMd5Sums.addAll(md5Sums);

        return OK;
    }

    @Override
    public List<String> getAllBucketObjects() {
        List<String> result = new ArrayList<>();
        try {
            ListObjectsRequest listObjects = ListObjectsRequest
                    .builder()
                    .bucket(awsS3BucketName)
                    .build();

            ListObjectsResponse res = s3Client.listObjects(listObjects);
            List<S3Object> objects = res.contents();

            result.addAll(
                    objects.stream()
                    .map(S3Object::key)
                    .collect(toList())
            );

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
        return result;
    }

    @Override
    public void deleteBucketObject(String key) {
        LOG.debug("Start deleting object {} from aws s3 bucket", key);
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder().bucket(awsS3BucketName).key(key).build();
        s3Client.deleteObject(deleteObjectRequest);
        LOG.debug("Deleting object {} from aws s3 bucket complete", key);
    }

    @Override
    public void deleteAllBucketObjects() {
        LOG.debug("Start deleting all data from aws s3 bucket");
        List<String> fileKeys = getAllBucketObjects();
        for (String key : fileKeys) {
            deleteBucketObject(key);
        }
        LOG.debug("Deleting all data from aws s3 bucket complete");
    }

    @Override
    public Set<String> getFilesMd5Sums() {
        return filesMd5Sums;
    }
}
