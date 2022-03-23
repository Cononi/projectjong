package com.winesee.projectjong.controller;

import com.winesee.projectjong.domain.board.PostRepository;
import com.winesee.projectjong.domain.board.dto.PostResponse;
import com.winesee.projectjong.domain.wine.WineRepository;
import com.winesee.projectjong.domain.wine.dto.WineRequest;
import com.winesee.projectjong.domain.wine.dto.WineResponse;
import com.winesee.projectjong.service.post.PostService;
import com.winesee.projectjong.service.wine.WineService;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
@Slf4j
public class Post {

    private final WineService wineService;
    private final PostService postService;

    @GetMapping("post/{number}")
    public String post(Model model, @PathVariable("number") Long number){
        WineResponse wine = wineService.wineGet(number);
        model.addAttribute("wineInfo", wine);
        return "/pages/post/post";
    }

    @GetMapping("post/info/{number}")
    public String info(Model model, @PathVariable("number") Long number){
        PostResponse post = postService.postGet(number);
        model.addAttribute("postInfo", post);
        return "/pages/post/postinfo";
    }
}
