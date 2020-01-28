package com.funny.memes.funnymemes.service;

import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface FileService {

    String downloadImage(String url);

    CompletableFuture<String> uploadMediaToS3(String fileName);

    CompletableFuture<String> getAllBucketObjectsAsync();

    List<String> getAllBucketObjects();

    List<String> getFilesMd5Sums();

    CompletableFuture<String> downloadImageAsync(String url);

    void deleteBucketObject(String key);

    void deleteAllBucketObjects();
}
