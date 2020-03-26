package com.funny.memes.funnymemes.service;

import com.funny.memes.funnymemes.entity.MemeList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MemeListService {

    List<MemeList> findAll();

    List<MemeList> findAllByLang(String lang);

    Page<MemeList> findAllByLang(String lang, Pageable pageable);

    List<String> getAllMd5Sum();
}
