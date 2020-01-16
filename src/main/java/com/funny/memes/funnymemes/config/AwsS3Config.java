package com.funny.memes.funnymemes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.services.s3.S3AsyncClient;

@Configuration
public class AwsS3Config {

    @Bean
    public S3AsyncClient s3Client() {
        return S3AsyncClient.builder().build();
    }
}
