package com.funny.memes.funnymemes.parsers;

import com.funny.memes.funnymemes.entity.Meme;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

/**
 * Author: Valentin Ershov
 * Date: 14.01.2020
 */
public interface ParseProcessor {

//    List<Meme> getRedditGroupsContent(String redditGroupName);

    void processRedditGroups();
}
