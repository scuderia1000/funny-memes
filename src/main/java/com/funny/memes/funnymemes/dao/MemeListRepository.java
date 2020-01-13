package com.funny.memes.funnymemes.dao;

import com.funny.memes.funnymemes.entity.MemeList;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemeListRepository extends MongoRepository<MemeList, String> {
}
