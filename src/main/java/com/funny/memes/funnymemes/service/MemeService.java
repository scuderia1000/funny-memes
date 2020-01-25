package com.funny.memes.funnymemes.service;

import com.funny.memes.funnymemes.entity.Meme;

import java.util.List;

public interface MemeService {

    Meme findById(String id);

    List<Meme> saveMemes(List<Meme> memes);
}
