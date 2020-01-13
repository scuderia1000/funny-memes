package com.funny.memes.funnymemes.service.impl;

import com.funny.memes.funnymemes.dao.MemeListRepository;
import com.funny.memes.funnymemes.entity.MemeList;
import com.funny.memes.funnymemes.service.MemeListService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MemeListServiceImpl implements MemeListService {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final MemeListRepository repository;

    @Autowired
    public MemeListServiceImpl(MemeListRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<MemeList> findAll() {
        List<MemeList> memes = repository.findAll();

        LOG.info("Memes count: {}", memes.size());
        return memes;
    }
}
