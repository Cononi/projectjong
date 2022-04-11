package com.winesee.projectjong.controller;

import com.winesee.projectjong.service.post.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.winesee.projectjong.config.constant.FileConstant.*;
import static com.winesee.projectjong.config.constant.FileConstant.PNG_EXTENSION;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;
import static org.springframework.http.MediaType.IMAGE_PNG_VALUE;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
@Slf4j
public class Notice {

    private final NoticeService noticeService;

    // 인포
    @GetMapping("notice/{number}")
    public String noticeList(Model model, @PathVariable("number") Long number){
        model.addAttribute("noticeInfo", noticeService.noticeGet(number));
        model.addAttribute("noticeList", noticeService.noticeList(0,5));
        return "pages/post/noticeInfo";
    }

    // 페이지
    @GetMapping("notice")
    public String noticeMain(Model model, @CookieValue(value = "_vi",required = false) String pages){
        if(StringUtils.isBlank(pages)){
            pages = "1";
        }
        model.addAttribute("thisPages", pages);
        model.addAttribute("noticePage", true);
        return "pages/post/notice";
    }

    // 작성 페이지
    @GetMapping("notice/post")
    public String noticePost(Model model, @CookieValue(value = "_vi",required = false) String pages) {
        return "pages/post/noticePost";
    }
}
