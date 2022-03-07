package com.winesee.projectjong.controller;

import com.winesee.projectjong.config.exception.EmailExistException;
import com.winesee.projectjong.config.exception.NotAnImageFileException;
import com.winesee.projectjong.config.exception.UserNotFoundException;
import com.winesee.projectjong.config.exception.UsernameExistException;
import com.winesee.projectjong.domain.user.dto.ProfileRequest;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.mail.MessagingException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

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


    /*-----------------------------------------------
    updateProfile - 프로필 업데이트 서비스 컨트롤러
    -----------------------------------------------*/
    @PutMapping(value = "mypage", produces = IMAGE_JPEG_VALUE)
    public String updateProfile(@AuthenticationPrincipal UserResponse userinfo,
                                @ModelAttribute("user") @Validated ProfileRequest user
                                ,BindingResult bindingResult
                                ,HttpSession session) throws UserNotFoundException, EmailExistException, IOException, UsernameExistException, NotAnImageFileException {
        if (bindingResult.hasErrors()) {
            log.info("실행불가");
            return "redirect:/account/mypage";
        }

        UserResponse userChange = userService.updateProfile(userinfo, user.getName(),user.getProfileImage());
        /* 변경된 세션 등록 */
        // 현재 사용자 정보를 없애기 위해. LocalThread(한 쓰레드내에 사용되는 공동 저장소) 에 있는 인증주체를 초기화 한다.
        // 사용자 인증정보인 Principal을 Authentication에서 관리하고 이것을 SecurityContext에서 관리한다. 이것을 총관리하는 주체가 SecurityContextHolder이다.
        // 그래서 SecurityContextHolder를 선언하면 모든 주체에 접근이 가능하다.
        // Context란 어떤 행위에 대해 핸들링하기 위한 접근 수단의 의미이다.
        SecurityContextHolder.clearContext();
        // Authentication에 유저의 접근 주체 토큰을 새로 생성한다.
        Authentication newAuthentication = new UsernamePasswordAuthenticationToken(userChange, null, userChange.getAuthorities());
        // SecurityContextHolder에서 관리하는 접근주체에 새로 생성된 유저의 정보를 담아 저장한다.
        SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        return "redirect:/account/mypage";
    }

    /*-----------------------------------------------
    mypageGet - View - 프로필 페이지
    -----------------------------------------------*/
    @GetMapping("mypage")
    public String mypageGet(@ModelAttribute("user") UserRequest user){
        return "pages/mypage/mypage";
    }

    /*-----------------------------------------------
    updateProfileAuth - 내 정보 진입전 비밀번호 확인
    -----------------------------------------------*/
    @PostMapping("mypage")
    public String updateProfileAuth(){
        return "redirect:/account/mypage";
    }

    /*-----------------------------------------------
    passChangeUpdate - 비밀번호 변경페이지 진입전 비밀번호 확인 페이지
    -----------------------------------------------*/
    @PostMapping("pass-change")
    public String passChangeUpdate(){
        return "redirect:/account/pass-change";
    }

    /*-----------------------------------------------
    passChangeGet - 비밀번호 페이지
    -----------------------------------------------*/
    @GetMapping("pass-change")
    public String passChangeGet(Model model
            ,@ModelAttribute("user") UserRequest user){
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

//    public String profileCheckSeuccess(HttpServletRequest request, HttpServletResponse response){
//        if(request.getMethod().equals("PUT")) {
//            log.info("수정 완료 쿠키 삭제 세션 삭제");
//            Cookie cookie = Arrays.stream(request.getCookies())
//                    .filter(c -> c.getName().equals("ppp_at"))
//                    .findAny()
//                    .orElseThrow(IllegalArgumentException::new);
//            cookie.setMaxAge(0);
//            response.addCookie(cookie);
//            request.getSession().removeAttribute("userProfilePasswordAuth");
//        }
//    }
}
