package com.funny.memes.funnymemes.service;

import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.util.concurrent.CompletableFuture;

public interface FileService {

    String downloadImage(String url);

    CompletableFuture<String> uploadMediaToS3(String fileName);
//    CompletableFuture<PutObjectResponse> uploadMediaToS3(String fileName);
//    String uploadMediaToS3(String fileName);
}
