package com.winesee.projectjong.controller;

import com.winesee.projectjong.config.exception.EmailExistException;
import com.winesee.projectjong.config.exception.UserNotFoundException;
import com.winesee.projectjong.config.exception.UsernameExistException;
import com.winesee.projectjong.domain.user.dto.UserRequest;
import com.winesee.projectjong.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.util.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
@Slf4j
public class User {

    private final UserService userService;

    /*-----------------------------------------------
    login - 로그인 페이지
    -----------------------------------------------*/
    @RequestMapping("login")
    public String login(Model model, @ModelAttribute("user") UserRequest user, HttpServletRequest request) {
        // 로그인 성공후 이전페이지로 리다이렉트.
        String referrer = request.getHeader("Referer");
        request.getSession().setAttribute("prevPage", referrer);
        return "pages/login";
    }

    /*-----------------------------------------------
    registerGet - 회원 가입 페이지
    -----------------------------------------------*/
    @GetMapping("register")
    public String registerGet(@ModelAttribute("user") UserRequest user) {
        return "pages/register";
    }

    /*-----------------------------------------------
    registerPost - 회원 가입 서비스 컨트롤러
    -----------------------------------------------*/
    @PostMapping("register")
    public String registerPost(@ModelAttribute("user") @Validated UserRequest user
            , BindingResult bindingResult, RedirectAttributes rtts)
            throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, MessagingException {
        // 메세지
        List<String> msg = new ArrayList<>();
        //** 에러 - 로직을 서비스 로직으로 옮길 필요가 있음.
        Errors errors = new BeanPropertyBindingResult(user,"user");
        bindingResult.addAllErrors(userService.userValidateCheck(errors, user));

        // 로직 변경해야댐.
        String[] names = { "username", "email", "name"};
        List<String> filedNames = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            for(FieldError field: bindingResult.getFieldErrors()){
                filedNames.add(field.getField());
                rtts.addFlashAttribute(field.getField(),true);
            }
            rtts.addFlashAttribute("regInputLoop", true);
            rtts.addFlashAttribute("user",user);
            return "redirect:/account/register";
        }
        userService.register(user);
        msg.add("가입인증 메일 발송");
        msg.add(user.getEmail()+"로 인증 확인 메일을 보냇습니다. <br/> 인증 확인후 로그인이 가능합니다.");
        rtts.addFlashAttribute("registerSummitMsg", msg);
        return "redirect:/account/message";
    }


    /*-----------------------------------------------
    emailConfirm - 이메일 가입 확인 인증
    -----------------------------------------------*/
    @GetMapping("email/sign/confirm")
    public String emailConfirm(@RequestParam("email") String email, @RequestParam("authId") String authId, @RequestParam("authKey") String authKey, Model model) throws MessagingException {
        // 서비스에서 유저 조회
        List<String> msg = userService.emailConfirm(email, authId, authKey);
        model.addAttribute("registerSummitMsg", msg);
        return "pages/message/register-message";
    }


}
