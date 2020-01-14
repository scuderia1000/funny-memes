package com.funny.memes.funnymemes.parsers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.Future;

@Component
public class AppListener implements ApplicationListener<ContextRefreshedEvent> {

    private final static Object lock = new Object();

    private volatile boolean alreadyStarted = false;

    @Autowired
    private MemeParseService memeParseService;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        synchronized (lock) {
            if (alreadyStarted) return;
            alreadyStarted = true;
        }

        memeParseService.initialize();
    }

    //    @Async
//    public void asyncMethodWithVoidReturnType() {
//        System.out.println("Execute method asynchronously. "
//                + Thread.currentThread().getName());
//    }
//
//    @Async
//    public Future<String> asyncMethodWithReturnType() {
//        System.out.println("Execute method asynchronously - "
//                + Thread.currentThread().getName());
//        try {
//            Thread.sleep(5000);
//            return new AsyncResult<String>("hello world !!!!");
//        } catch (InterruptedException e) {
//            //
//        }
//
//        return null;
//    }
//
//    @Async
//    public void asyncMethodWithExceptions() throws Exception {
//        throw new Exception("Throw message from asynchronous method. ");
//    }
}
