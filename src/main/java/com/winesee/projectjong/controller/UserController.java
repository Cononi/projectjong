package com.winesee.projectjong.controller;

import com.winesee.projectjong.config.exception.EmailExistException;
import com.winesee.projectjong.config.exception.UserNotFoundException;
import com.winesee.projectjong.config.exception.UsernameExistException;
import com.winesee.projectjong.domain.user.User;
import com.winesee.projectjong.domain.user.dto.UserRequest;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import com.winesee.projectjong.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.security.Principal;
import java.util.*;

import static org.springframework.http.HttpStatus.OK;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
@Slf4j
public class UserController {

    private final UserService userService;
    private final Validator validator;

    @RequestMapping("login")
    public String login(Model model, @ModelAttribute("user") UserRequest user, HttpServletRequest request, @AuthenticationPrincipal UserResponse userinfo) {
        if(ObjectUtils.isNotEmpty(userinfo)){
            return "redirect:/";
        }
        if(!request.getRequestURI().equals("/account/login")){
            String referrer = request.getHeader("Referer");
            request.getSession().setAttribute("prevPage", referrer);
        }
        return "pages/login";
    }

    @GetMapping("register")
    public String registerGet(@ModelAttribute("user") UserRequest user,@AuthenticationPrincipal UserResponse userinfo) {
        if(ObjectUtils.isNotEmpty(userinfo)){
            return "redirect:/";
        }
        return "pages/register";
    }

    @PostMapping("register")
    public String registerPost(Model model, @ModelAttribute("user") @Validated UserRequest user
            , BindingResult bindingResult, RedirectAttributes rtts)
            throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, MessagingException {
        // 메세지
        List<String> msg = new ArrayList<>();
        //** 에러 - 로직을 서비스 로직으로 옮길 필요가 있음.
        Errors errors = new BeanPropertyBindingResult(user,"user");
        bindingResult.addAllErrors(userService.userValidateCheck(errors, user));

        String[] names = { "username", "email", "name"};
        List<String> filedNames = new ArrayList<>();
        if (bindingResult.hasErrors()) {
            for(FieldError field: bindingResult.getFieldErrors()){
                filedNames.add(field.getField());
                model.addAttribute(field.getField(),true);
            }
            for (String name : names) {
                for (String filedName : filedNames) {
                    if (!name.equals(filedName)) {
                        model.addAttribute(name + "in", true);
                        break;
                    }
                }
            }
            return "pages/register";
        }
        userService.register(user);
        msg.add("가입인증 메일 발송");
        msg.add(user.getEmail()+"로 인증 확인 메일을 보냇습니다. <br/> 인증 확인후 로그인이 가능합니다.");
        rtts.addFlashAttribute("registerSummitMsg", msg);
        return "redirect:/account/message";
    }

    /*
    아이디 인증
     */
    @GetMapping(path = {"find/email/{username}", "find/username/{username}", "find/name/{username}"}, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUser(@PathVariable("username") String username, HttpServletRequest request) throws UserNotFoundException, EmailExistException, UsernameExistException {
        return new ResponseEntity<>(userService.userCheck(username,request.getRequestURI()), OK);
    }

    // 이메일 가입
    @GetMapping("email/sign/confirm")
    public String emailConfirm(@RequestParam("email") String email, @RequestParam("authId") String authId, @RequestParam("authKey") String authKey, Model model) throws MessagingException {
        // 서비스에서 유저 조회
        List<String> msg = userService.emailConfirm(email, authId, authKey);
        model.addAttribute("registerSummitMsg", msg);
        return "pages/message/register-message";
    }

    @GetMapping("mypage")
    public String mypage(@AuthenticationPrincipal UserResponse userinfo, Model model){
        model.addAttribute("userinfo",userinfo);
        return "pages/mypage";
    }

    @GetMapping("message")
    public String message(){
        return "pages/message/register-message";
    }
}
