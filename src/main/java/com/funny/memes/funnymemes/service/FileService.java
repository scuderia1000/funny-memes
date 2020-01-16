package com.funny.memes.funnymemes.service;

public interface FileService {

    byte[] downloadImage(String url);

    void uploadMediaToS3(String fileName);
}
