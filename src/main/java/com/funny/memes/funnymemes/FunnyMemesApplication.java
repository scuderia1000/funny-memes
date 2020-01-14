package com.funny.memes.funnymemes;

import com.funny.memes.funnymemes.dao.MemeRepository;
import com.funny.memes.funnymemes.entity.Meme;
import com.funny.memes.funnymemes.parsers.AppListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

@SpringBootApplication
public class FunnyMemesApplication implements CommandLineRunner {

	private final static Logger LOG = LoggerFactory.getLogger(FunnyMemesApplication.class);

	@Autowired
	private MemeRepository repository;

	@Autowired
	private AppListener memesParserThread;

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

//		testAsyncAnnotationForMethodsWithReturnType();
//		testAsyncAnnotationForMethodsWithException();
	}

//
//	public void testAsyncAnnotationForMethodsWithReturnType()
//			throws InterruptedException, ExecutionException {
//		System.out.println("Invoking an asynchronous method. "
//				+ Thread.currentThread().getName());
//
//		Future<String> future = memesParserThread.asyncMethodWithReturnType();
//
//		while (true) {
//			if (future.isDone()) {
//				System.out.println("Result from asynchronous process - " + future.get());
//				break;
//			}
//			System.out.println("Continue doing something else. ");
//			Thread.sleep(1000);
//		}
//	}
//
//	public void testAsyncAnnotationForMethodsWithException() throws Exception {
//		System.out.println("Start - invoking an asynchronous method. ");
//		memesParserThread.asyncMethodWithExceptions();
//		System.out.println("End - invoking an asynchronous method. ");
//	}


}
