package com.funny.memes.funnymemes;

import com.funny.memes.funnymemes.dao.MemeRepository;
import com.funny.memes.funnymemes.entity.Meme;
import com.funny.memes.funnymemes.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import software.amazon.awssdk.services.s3.S3Client;

import java.util.Date;
import java.util.List;

@SpringBootApplication
public class FunnyMemesApplication {

//	private final static Logger LOG = LoggerFactory.getLogger(FunnyMemesApplication.class);
//
//	@Autowired
//	private MemeRepository repository;
//
//	@Autowired
//	private FileService fileService;

	public static void main(String[] args) {
		SpringApplication.run(FunnyMemesApplication.class, args);
	}

//	@Override
//	public void run(String... args) {
//		LOG.debug("Deleting all memes from repository");
//		repository.deleteAll();
//		LOG.debug("Deleting all memes from repository complete");
//
//		LOG.debug("Deleting all data from aws s3");
//		List<String> fileKeys = fileService.getAllBucketObjects();
//		for (String key : fileKeys) {
//			fileService.deleteBucketObject(key);
//		}
//		LOG.debug("Deleting all data from aws s3 complete");
//	}

}
