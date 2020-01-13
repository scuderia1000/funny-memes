package com.funny.memes.funnymemes.service.impl;

import com.funny.memes.funnymemes.dao.MemeRepository;
import com.funny.memes.funnymemes.entity.Meme;
import com.funny.memes.funnymemes.service.MemeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MemeServiceImpl implements MemeService {

    private final Logger LOG = LoggerFactory.getLogger(getClass());

    private final MemeRepository repository;

    @Autowired
    public MemeServiceImpl(MemeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Meme findById(String id) {
        Meme meme = repository.findById(id).orElse(new Meme());

        LOG.info("Found meme: " + meme);
        return meme;
    }
}
