package com.winesee.projectjong.controller;

import com.winesee.projectjong.service.wine.WineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
@Slf4j
public class Wine {

    private final WineService wineService;

    @GetMapping(value = "wine/{page}")
    public String wineListMain(@PathVariable("page") Integer pages, Model model) {
        model.addAttribute("wineList",wineService.wineAll(pages));

        return "pages/wine/winelist";
    }
}
