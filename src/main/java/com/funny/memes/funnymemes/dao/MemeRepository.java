package com.funny.memes.funnymemes.dao;

import com.funny.memes.funnymemes.entity.Meme;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MemeRepository extends MongoRepository<Meme, String> {
}
