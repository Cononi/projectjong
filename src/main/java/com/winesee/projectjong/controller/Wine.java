package com.winesee.projectjong.controller;

import com.winesee.projectjong.domain.wine.Search;
import com.winesee.projectjong.service.wine.WineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
@Slf4j
public class Wine {

    private final WineService wineService;

    @GetMapping(value = "wine")
    public String wineListMain(@ModelAttribute Search search, Model model) throws IllegalAccessException {
        if(StringUtils.isBlank(search.getQuery()))
            return "pages/message/search-message";
        model.addAttribute("wineList",wineService.wineAll(search));
        return "pages/wine/winelist";
    }


    @RequestMapping(value = "wine/{page}/{index}")
    public String wineListMain(Model model, HttpServletRequest request, @PathVariable("page") String page, @PathVariable("index") Long index){
        String referer = (String)request.getHeader("REFERER");
        log.info(referer);
        if(referer != null){
            if(referer.contains("wine?")) {
                model.addAttribute("backLink", referer);
            }
        }
        model.addAttribute("wineInfo", wineService.wineGet(index));
        model.addAttribute("thisPage", page);
        return "pages/wine/wineinfo";
    }
}
