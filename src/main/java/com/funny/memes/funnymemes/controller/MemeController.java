package com.funny.memes.funnymemes.controller;

import com.funny.memes.funnymemes.entity.Meme;
import com.funny.memes.funnymemes.entity.MemeList;
import com.funny.memes.funnymemes.service.MemeListService;
import com.funny.memes.funnymemes.service.MemeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Controller
//@RestController
public class MemeController {

    private final static Logger LOG = LoggerFactory.getLogger(MemeController.class);

    private final MemeService memeService;

    @Autowired
    private MemeListService memeListService;

    @Autowired
    public MemeController(MemeService memeService) {
        this.memeService = memeService;
    }

//    @RequestMapping(value = "/", method = RequestMethod.GET)
//    public ResponseEntity<Page<MemeList>> getMemesPaged(
//                                        @RequestParam("page") Optional<Integer> page,
//                                        @RequestParam("size") Optional<Integer> size) {
//        LOG.info("Get memes paged request");
//
//        Locale locale = LocaleContextHolder.getLocale();
//
//        int currentPage = page.orElse(1);
//        int pageSize = size.orElse(5);
//
//        Page<MemeList> memesPage = memeListService.findAllByLang(locale.toString(), PageRequest.of(currentPage - 1, pageSize));
//
////        List<MemeList> memes = memeListService.findAllByLang(locale.toString());
////        model.addAttribute("memesPage", memesPage);
////        model.addAttribute("memes", memes);
//
////        int totalPages = memesPage.getTotalPages();
////        if (totalPages > 0) {
////            List<Integer> pageNumbers = IntStream.rangeClosed(1, totalPages)
////                    .boxed()
////                    .collect(Collectors.toList());
////            model.addAttribute("pageNumbers", pageNumbers);
////        }
//
//        return ResponseEntity.ok(memesPage);
//    }

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

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String getMemesPaged(Model model,
                                @RequestParam("page") Optional<Integer> page,
                                @RequestParam("size") Optional<Integer> size) {
        LOG.info("Get memes paged request");

        Locale locale = LocaleContextHolder.getLocale();

        int currentPage = page.orElse(1);
        int pageSize = size.orElse(5);

        Page<MemeList> memesPage = memeListService.findAllByLang(locale.toString(), PageRequest.of(currentPage - 1, pageSize));

//        List<MemeList> memes = memeListService.findAllByLang(locale.toString());
        model.addAttribute("memesPage", memesPage);
//        model.addAttribute("memes", memes);

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
    public Meme getMeme(@PathVariable String id) {
        LOG.info("Get meme by id request: {}", id);
        return memeService.findById(id);
    }
}
