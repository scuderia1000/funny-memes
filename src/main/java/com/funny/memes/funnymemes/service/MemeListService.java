package com.funny.memes.funnymemes.service;

import com.funny.memes.funnymemes.entity.MemeList;

import java.util.List;

public interface MemeListService {

    List<MemeList> findAll();

    List<MemeList> findAllByLang(String lang);
}
