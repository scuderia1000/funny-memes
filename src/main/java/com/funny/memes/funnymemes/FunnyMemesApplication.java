package com.funny.memes.funnymemes;

import com.funny.memes.funnymemes.dao.MemeRepository;
import com.funny.memes.funnymemes.entity.Meme;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;
import java.util.List;

@SpringBootApplication
public class FunnyMemesApplication implements CommandLineRunner {

	private final static Logger LOG = LoggerFactory.getLogger(FunnyMemesApplication.class);

	@Autowired
	private MemeRepository repository;

//	@Autowired
//	private AmazonS3 s3Client;

	@Value("#{'${reddit.group}'.split(',')}")
	private List<String> redditGroups;

	public static void main(String[] args) {
		SpringApplication.run(FunnyMemesApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		LOG.info("deleteAll from repository");
		repository.deleteAll();
	}

}
