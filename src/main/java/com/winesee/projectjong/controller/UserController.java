package com.winesee.projectjong.controller;

import com.winesee.projectjong.config.exception.EmailExistException;
import com.winesee.projectjong.config.exception.UserNotFoundException;
import com.winesee.projectjong.config.exception.UsernameExistException;
import com.winesee.projectjong.domain.user.dto.UserRequest;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import com.winesee.projectjong.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
@Slf4j
public class UserController {

    private final UserService userService;

    @RequestMapping("login")
    public String login(Model model, @ModelAttribute("user") UserRequest user, HttpServletRequest request) {
        if(!request.getRequestURI().equals("/account/login")){
            String referrer = request.getHeader("Referer");
            request.getSession().setAttribute("prevPage", referrer);
        }
        return "pages/login";
    }

    @GetMapping("register")
    public String registerGet(Model model, @ModelAttribute("user") UserRequest user, HttpServletRequest request) {
        return "pages/register";
    }

    @PostMapping("register")
    public String registerPost(Model model, @ModelAttribute("user") @Validated UserRequest user, BindingResult bindingResult) {
        // 유효성 검사.
        if (bindingResult.hasErrors()) {
            model.addAttribute("isError",true);
            return "pages/register";
        }
        return "pages/register";
    }

    @GetMapping(path = {"/find/email/{username}", "/find/username/{username}", "/find/name/{username}"}, produces= MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUser(@PathVariable("username") String username, HttpServletRequest request) throws UserNotFoundException, EmailExistException, UsernameExistException, UserNotFoundException, EmailExistException, UsernameExistException {
        return new ResponseEntity<>(userService.userCheck(username,request.getRequestURI()), OK);
    }

    @GetMapping("mypage")
    public String mypage(Principal principal, Model model){
        if(principal != null){
            UserResponse userinfo = new UserResponse(userService.findByUsername(principal.getName()));
            model.addAttribute("userinfo",userinfo);
        }
        return "pages/mypage";
    }
}
