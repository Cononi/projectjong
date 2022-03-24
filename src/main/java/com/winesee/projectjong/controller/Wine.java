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


    @RequestMapping(value = "wine/{page}/{index}/{tastingPage}")
    public String wineListMain(Model model, HttpServletRequest request, @PathVariable("page") String page, @PathVariable("index") Long index,
                               @PathVariable("tastingPage") Long tastingPage){
        String referer = (String)request.getHeader("REFERER");
        // 널이 아닐경우 이전 페이지 정보를 가져옴.
        if(referer != null){
            if(referer.contains("wine?")) {
                model.addAttribute("backLink", referer);
            }
        }
        // 테스팅 리스트에 페이징 번호.
        model.addAttribute("tastingPage",tastingPage);
        // 와인 정보.
        model.addAttribute("wineInfo", wineService.wineGet(index));
        // 이전 와인 리스트에 페이지 번호.
        model.addAttribute("thisPage", page);
        return "pages/wine/wineinfo";
    }
}
