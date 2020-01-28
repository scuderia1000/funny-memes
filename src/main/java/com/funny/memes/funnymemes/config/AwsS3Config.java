package com.funny.memes.funnymemes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsS3Config {

    @Bean
    public S3AsyncClient s3AsyncClient() {
        return S3AsyncClient.builder().build();
    }

    @Bean
    public S3Client s3Client() {
        return S3Client.builder().build();
    }
}
