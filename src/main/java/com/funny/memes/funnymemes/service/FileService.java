package com.funny.memes.funnymemes.service;

import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface FileService {

    String downloadImage(String url);

    CompletableFuture<String> uploadMediaToS3(String fileName);

    CompletableFuture<String> getAllBucketObjects();

    List<String> getFilesMd5Sums();
}
