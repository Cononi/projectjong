package com.winesee.projectjong.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;


@Controller
@RequestMapping("/")
@Slf4j
public class Main {

    @RequestMapping("/")
    public String main(HttpServletRequest request){

        log.info(request.getRequestURI());
        return "pages/index";
    }

}
