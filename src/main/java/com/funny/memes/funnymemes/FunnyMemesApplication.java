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

	private final Logger LOG = LoggerFactory.getLogger(getClass());

	@Autowired
	private MemeRepository repository;

	@Value("#{'${reddit.group}'.split(',')}")
	private List<String> redditGroups;

	public static void main(String[] args) {
		SpringApplication.run(FunnyMemesApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		repository.deleteAll();

		LOG.info("Create new meme");

		Meme meme = new Meme();
		meme.setName("First meme");
		meme.setImagePath("http://test.com/image_1.jpg");
		meme.setDescription("First meme description");
		meme.setPublishDate(new Date());

		repository.save(meme);

		LOG.info("Meme saved: {}", meme.toString());

		for (String groupName : redditGroups) {
			LOG.info("Reddit group name: {}", groupName);
		}
	}
}
