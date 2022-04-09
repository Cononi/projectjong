package com.winesee.projectjong.controller;

import com.winesee.projectjong.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/")
@RequiredArgsConstructor
public class Main {

    private final PostService postService;

    @RequestMapping("/")
    public String main(Model model){
        model.addAttribute("bestList",postService.bestPostCommentList());
        return "pages/index";
    }

}
