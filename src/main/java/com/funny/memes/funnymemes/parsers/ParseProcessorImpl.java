package com.funny.memes.funnymemes.parsers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * Author: Valentin Ershov
 * Date: 14.01.2020
 */
@Component
public class ParseProcessorImpl implements ParseProcessor {

    private final static Logger LOG = LoggerFactory.getLogger(ParseProcessorImpl.class);

    @Async
    @Override
    public void startParseProcessing(String redditGroupName) {
        LOG.debug("ParseProcessor ({}): Start parsing reddit group \"{}\"", Thread.currentThread().getName(), redditGroupName);
        try {
            Thread.sleep(1000);
//            return new AsyncResult<String>("hello world !!!!");
        } catch (InterruptedException e) {
            //
        }
        LOG.debug("ParseProcessor ({}): Parsing reddit group \"{}\" completed", Thread.currentThread().getName(), redditGroupName);
//        return null;
    }
}
