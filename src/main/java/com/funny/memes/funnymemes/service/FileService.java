package com.funny.memes.funnymemes.service;

import com.funny.memes.funnymemes.dto.PutObjectResponseDto;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public interface FileService {

    String downloadImage(String url);

    CompletableFuture<PutObjectResponseDto> uploadMediaToS3(String fileName, String lang);

    CompletableFuture<String> getAllBucketObjectsAsync();

    List<String> getAllBucketObjects();

    Set<String> getFilesMd5Sums();

    CompletableFuture<String> downloadImageAsync(String url);

    void deleteBucketObject(String key);

    void deleteAllBucketObjects();

    String updateMd5SumList();
}
