package com.funny.memes.funnymemes.service.impl;

import com.funny.memes.funnymemes.dao.MemeListRepository;
import com.funny.memes.funnymemes.entity.MemeList;
import com.funny.memes.funnymemes.service.MemeListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class MemeListServiceImpl implements MemeListService {

    private final static Logger LOG = LoggerFactory.getLogger(MemeListServiceImpl.class);

    private final MemeListRepository repository;

    @Autowired
    public MemeListServiceImpl(MemeListRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<MemeList> findAll() {
        List<MemeList> memes = repository.findAll(Sort.by(Sort.Direction.DESC, "publishDate"));

        LOG.info("Memes count: {}", memes.size());
        return memes;
    }

    @Override
    public List<MemeList> findAllByLang(String lang) {
        LOG.info("MemeListServiceImpl findAllByLang: {}", lang);

        List<MemeList> memes = repository.findByLangOrderByPublishDateDesc(lang);

        LOG.info("MemeListServiceImpl findAllByLang {} complete execute query, memes count: {}", lang, memes.size());

        return memes;
    }

    @Override
    public Page<MemeList> findAllByLang(String lang, Pageable pageable) {
        LOG.info("MemeListServiceImpl findAllByLang pageable: {}", lang);

        Page<MemeList> memes = repository.findByLangOrderByPublishDateDesc(lang, pageable);

        LOG.info("MemeListServiceImpl findAllByLang pageable {} complete execute query, memes Total Elements: {}", lang, memes.getTotalElements());

        return memes;
    }

    @Override
    public List<String> getAllMd5Sum() {
        List<MemeList> memeLists = repository.findByIdNotNull();

        return memeLists.stream().map(MemeList::getMd5Sum).collect(Collectors.toList());
    }
}
