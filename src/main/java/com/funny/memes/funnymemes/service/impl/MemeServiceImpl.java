package com.funny.memes.funnymemes.service.impl;

import com.funny.memes.funnymemes.dao.MemeRepository;
import com.funny.memes.funnymemes.entity.Meme;
import com.funny.memes.funnymemes.service.MemeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MemeServiceImpl implements MemeService {

    private final static Logger LOG = LoggerFactory.getLogger(MemeServiceImpl.class);

    private final MemeRepository repository;

    @Autowired
    public MemeServiceImpl(MemeRepository repository) {
        this.repository = repository;
    }

    @Override
    public Meme findById(String id) {
        Meme meme = repository.findById(id).orElse(new Meme());

        LOG.info("Found meme: {}", meme.toString());
        return meme;
    }

    @Override
    @Transactional
    public List<Meme> saveMemes(List<Meme> memes) {
        List<Meme> savedMemes = repository.insert(memes);

        LOG.info("Saved memes count: {}", savedMemes.size());

        return savedMemes;
    }
}
