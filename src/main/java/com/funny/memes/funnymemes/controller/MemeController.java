package com.funny.memes.funnymemes.controller;

import com.funny.memes.funnymemes.entity.Meme;
import com.funny.memes.funnymemes.entity.MemeList;
import com.funny.memes.funnymemes.service.MemeListService;
import com.funny.memes.funnymemes.service.MemeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/feed")
public class MemeController {

    private final static Logger LOG = LoggerFactory.getLogger(MemeController.class);

    private final MemeService memeService;

    @Autowired
    private MemeListService memeListService;

    @Autowired
    public MemeController(MemeService memeService) {
        this.memeService = memeService;
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getMemes(Model model) {
        LOG.info("Get memes request");

        List<MemeList> memes = memeListService.findAll();
        model.addAttribute("message", "World!");
        return "index";
    }

    @RequestMapping(value = "/:{id}", method = RequestMethod.GET)
    public Meme getMeme(@PathVariable String id) {
        LOG.info("Get meme by id request: {}", id);
        return memeService.findById(id);
    }
}
