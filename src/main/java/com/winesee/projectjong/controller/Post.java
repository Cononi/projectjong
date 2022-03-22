package com.winesee.projectjong.controller;

import com.winesee.projectjong.domain.wine.WineRepository;
import com.winesee.projectjong.domain.wine.dto.WineRequest;
import com.winesee.projectjong.domain.wine.dto.WineResponse;
import com.winesee.projectjong.service.wine.WineService;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/wine")
@Slf4j
public class Post {

    private final WineService wineService;

    @RequestMapping("post/{number}")
    public String post(Model model, @PathVariable("number") Long number){
        WineResponse wine = wineService.wineGet(number);
        model.addAttribute("wineInfo", wine);
        return "/pages/post/post";
    }
}
