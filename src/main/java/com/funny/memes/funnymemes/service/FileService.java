package com.funny.memes.funnymemes.service;

public interface FileService {

    String downloadImage(String url);

    String uploadMediaToS3(String fileName);
}
