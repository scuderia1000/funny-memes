package com.funny.memes.funnymemes.dao;

import com.funny.memes.funnymemes.entity.MemeList;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MemeListRepository extends MongoRepository<MemeList, String> {

    List<MemeList> findByLangOrderByPublishDateDesc(String lang);
}
