package com.winesee.projectjong.controller;

import com.winesee.projectjong.config.exception.EmailExistException;
import com.winesee.projectjong.config.exception.NotAnImageFileException;
import com.winesee.projectjong.config.exception.UserNotFoundException;
import com.winesee.projectjong.config.exception.UsernameExistException;
import com.winesee.projectjong.domain.user.dto.UserRequest;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import com.winesee.projectjong.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import java.io.IOException;
import java.util.*;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
@Slf4j
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;

    /*-----------------------------------------------
    login - 로그인 페이지
    -----------------------------------------------*/
    @RequestMapping("login")
    public String login(Model model, @ModelAttribute("user") UserRequest user, HttpServletRequest request) {
        // 로그인 성공후 이전페이지로 리다이렉트.
        if(!request.getRequestURI().equals("/account/login")){
            String referrer = request.getHeader("Referer");
            request.getSession().setAttribute("prevPage", referrer);
        }
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
    public String registerPost(Model model, @ModelAttribute("user") @Validated UserRequest user
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


    /*-----------------------------------------------
    updateProfile - 프로필 업데이트 서비스 컨트롤러
    -----------------------------------------------*/
    @PutMapping(value = "mypage", produces = IMAGE_JPEG_VALUE)
    public String updateProfile(@AuthenticationPrincipal UserResponse userinfo,
                                @RequestParam("username") String username,
                                @RequestParam("name") String name,
                                @RequestParam(value = "email") String email,
                                @RequestParam(value = "profileImage") MultipartFile profileImage
                                ,HttpSession session) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotAnImageFileException {
        userService.updateProfile(userinfo,username, name, email,profileImage);

        /* 변경된 세션 등록 */
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(userinfo.getUsername(), session.getAttribute("userProfilePasswordAuth")));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        session.removeAttribute("userProfilePasswordAuth");
        return "redirect:/account/mypage";
    }

    /*-----------------------------------------------
    mypageGet - View - 프로필 페이지
    -----------------------------------------------*/
    @GetMapping("mypage")
    public String mypageGet(Model model
            ,@ModelAttribute("user") UserRequest user, RedirectAttributes rtts){
        model.addAttribute("thisMypageActive","mypage");
        return "pages/mypage/mypage";
    }


    /*-----------------------------------------------
    updateProfileAuth - 내 정보 진입전 비밀번호 확인
    -----------------------------------------------*/
    @PostMapping("mypage")
    public String updateProfileAuth(@AuthenticationPrincipal UserResponse userinfo, @RequestParam("passwordAuth") String inputPassword
            ,HttpSession session,RedirectAttributes rtts, HttpServletRequest request){
        return passwordCheck(userinfo,inputPassword,session,rtts, request.getRequestURI());
    }

    /*-----------------------------------------------
    passChangeUpdate - 비밀번호 변경페이지 진입전 비밀번호 확인 페이지
    -----------------------------------------------*/
    @PostMapping("mypage/pass-change")
    public String passChangeUpdate(@AuthenticationPrincipal UserResponse userinfo, @RequestParam("passwordAuth") String inputPassword
            ,HttpSession session,RedirectAttributes rtts){

        boolean check = userService.passwordAuth(userinfo.getPassword(),inputPassword);
        log.info(String.valueOf(check));
        if(check){
            session.setAttribute("userProfilePasswordAuth", inputPassword);
            session.setMaxInactiveInterval(300);
            rtts.addFlashAttribute("profileSuccess", check);
        }
        return "redirect:/account/pass-change";
    }

    /*-----------------------------------------------
    passChangeGet - 비밀번호 페이지
    -----------------------------------------------*/
    @GetMapping("mypage/pass-change")
    public String passChangeGet(@AuthenticationPrincipal UserResponse userinfo, Model model
            ,@ModelAttribute("user") UserRequest user){
        model.addAttribute("userinfo",userinfo);
        model.addAttribute("thisMypageActive","mypagePasswordChange");
        return "pages/mypage/pass";
    }

    @GetMapping("message")
    public String message(){
        return "pages/message/register-message";
    }

    public String errorEditUserCheck(HttpSession session, String uri, RedirectAttributes rtts){
        // 세션이 유효하다면 비밀번호 확인페이지로 접속하지 않아도 됨.
        if(ObjectUtils.isNotEmpty(session.getAttribute("userProfilePasswordAuth"))){
            rtts.addFlashAttribute("profileSuccess", true);
            return "redirect:" +uri;
        }
        return "";
    }

    public String passwordCheck(UserResponse userinfo, String inputPassword, HttpSession session, RedirectAttributes rtts, String uri){
        // 세션 인증시간 만료시 다시 비밀번호 체크.
        boolean check = userService.passwordAuth(userinfo.getPassword(),inputPassword);
        if(check){
            session.setAttribute("userProfilePasswordAuth", inputPassword);
            session.setMaxInactiveInterval(300);
            rtts.addFlashAttribute("profileSuccess", true);
        }
        return "redirect:" +uri;
    }
}
