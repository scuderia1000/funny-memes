package com.funny.memes.funnymemes.dao;

import com.funny.memes.funnymemes.entity.MemeList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MemeListRepository extends MongoRepository<MemeList, String> {

    List<MemeList> findByLangOrderByPublishDateDesc(String lang);

    Page<MemeList> findByLangOrderByPublishDateDesc(String lang, Pageable pageable);

    @Query(fields = "{ 'md5Sum' : 1 }")
    List<MemeList> findByIdNotNull();
}
