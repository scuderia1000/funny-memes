package com.funny.memes.funnymemes.controller;

import com.funny.memes.funnymemes.entity.Meme;
import com.funny.memes.funnymemes.entity.MemeList;
import com.funny.memes.funnymemes.service.MemeListService;
import com.funny.memes.funnymemes.service.MemeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
//@RestController
public class MemeController {

    private final static Logger LOG = LoggerFactory.getLogger(MemeController.class);

    private static final int DEFAULT_PAGE_SIZE = 5;

    private final MemeService memeService;

    @Autowired
    private MemeListService memeListService;

    @Autowired
    public MemeController(MemeService memeService) {
        this.memeService = memeService;
    }

    @RequestMapping(value = "/page{number:\\d+}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<Page<MemeList>> getMemesPaged(
                                        @PathVariable("number") Optional<String> page,
                                        @RequestParam("size") OptionalInt size,
                                        Locale locale) {
        LOG.info("Get memes paged request");

        int currentPage = Integer.parseInt(page.orElse("1"));
        int pageSize = size.orElse(DEFAULT_PAGE_SIZE);

        Page<MemeList> memesPage = memeListService.findAllByLang(locale.toString(), PageRequest.of(currentPage - 1, pageSize));

        return ResponseEntity.ok(memesPage);
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getMemesPaged(Model model, Locale locale) {
        LOG.info("Get memes paged request");

        int currentPage = 1;

        Page<MemeList> memesPage = memeListService.findAllByLang(locale.toString(), PageRequest.of(currentPage - 1, DEFAULT_PAGE_SIZE));

        model.addAttribute("memesPage", memesPage);

        int totalPages = memesPage.getTotalPages();
        if (totalPages > 0) {
            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
                    .boxed()
                    .collect(Collectors.toList());
            model.addAttribute("pageNumbers", pageNumbers);
        }

        return "index";
    }

    @RequestMapping(value = "/post/{id}", method = RequestMethod.GET)
    public String getMeme(@PathVariable String id, Model model) {
        LOG.info("Get meme by id request: {}", id);

        Meme meme = memeService.findById(id);

        model.addAttribute("meme", meme);

        return "meme";
    }

    //    @RequestMapping(value = "/", method = RequestMethod.GET)
//    public String getMemes(Model model) {
//        LOG.info("Get memes request");
//
//        Locale locale = LocaleContextHolder.getLocale();
//
//        List<MemeList> memes = memeListService.findAllByLang(locale.toString());
//        model.addAttribute("memes", memes);
//
//        return "index";
//    }
}
