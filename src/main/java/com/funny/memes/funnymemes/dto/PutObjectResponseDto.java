package com.funny.memes.funnymemes.dto;

public class PutObjectResponseDto {

    private String md5Sum;

    private String s3Url;

    private String responseText;

    public PutObjectResponseDto(String responseText, String md5Sum, String s3Url) {
        this.md5Sum = md5Sum;
        this.s3Url = s3Url;
        this.responseText = responseText;
    }

    public PutObjectResponseDto(String responseText) {
        this.responseText = responseText;
    }

    public String getS3Url() {
        return s3Url;
    }

    public void setS3Url(String s3Url) {
        this.s3Url = s3Url;
    }

    public String getResponseText() {
        return responseText;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public String getMd5Sum() {
        return md5Sum;
    }

    public void setMd5Sum(String md5Sum) {
        this.md5Sum = md5Sum;
    }
}
