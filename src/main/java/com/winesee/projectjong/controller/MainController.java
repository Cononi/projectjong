package com.winesee.projectjong.controller;

import com.winesee.projectjong.domain.user.Role;
import com.winesee.projectjong.domain.user.User;
import com.winesee.projectjong.domain.user.dto.UserRequest;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import com.winesee.projectjong.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
@Slf4j
public class MainController {

    private final UserService userService;

    @RequestMapping("/")
    public String main(@AuthenticationPrincipal UserResponse userinfo, Model model){
        model.addAttribute("userinfo",userinfo);
        return "pages/index";
    }

}
