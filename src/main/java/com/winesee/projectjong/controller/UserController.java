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

import javax.servlet.http.HttpServletRequest;

import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    public String registerPost(Model model, @ModelAttribute("user") @Validated UserRequest user, BindingResult bindingResult) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException {

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
        userService.regiter(user);
        return "redirect:/";
    }

    @GetMapping(path = {"/find/email/{username}", "/find/username/{username}", "/find/name/{username}"}, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUser(@PathVariable("username") String username, HttpServletRequest request) throws UserNotFoundException, EmailExistException, UsernameExistException {
        return new ResponseEntity<>(userService.userCheck(username,request.getRequestURI()), OK);
    }

    @GetMapping("mypage")
    public String mypage(@AuthenticationPrincipal UserResponse userinfo, Model model){
        model.addAttribute("userinfo",userinfo);
        return "pages/mypage";
    }
}
