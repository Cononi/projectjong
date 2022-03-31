package com.winesee.projectjong.controller;

import com.winesee.projectjong.config.exception.*;
import com.winesee.projectjong.domain.user.dto.PasswordChangeRequest;
import com.winesee.projectjong.domain.user.dto.ProfileRequest;
import com.winesee.projectjong.domain.user.dto.UserRequest;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import com.winesee.projectjong.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
@Slf4j
public class Profile {

    private final UserService userService;

    /*-----------------------------------------------
     updateProfile - 프로필 업데이트 서비스 컨트롤러
     -----------------------------------------------*/
    @PutMapping(value = "mypage", produces = IMAGE_JPEG_VALUE)
    public String updateProfile(@AuthenticationPrincipal UserResponse userinfo,
                                @ModelAttribute("user") @Validated ProfileRequest user,
                                BindingResult bindingResult
                                ,RedirectAttributes rtts) throws IOException, NotAnImageFileException, ProfileErrorException {
        // 입력이 오류일 경우.
        if (bindingResult.hasErrors()) {
            rtts.addFlashAttribute("errorProfileMsg", bindingResult.getFieldError().getDefaultMessage());
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


    /*-----------------------------------------------------------------------------------------------------------------------
    ------------------------------------------------------------------------------------------------------------------------ */


    /*-----------------------------------------------
      updatePassword - 비밀번호 업데이트
      -----------------------------------------------*/
    @PutMapping(value = "pass-change")
    public String updatePassword(@AuthenticationPrincipal UserResponse userinfo,
                                @ModelAttribute("userPassChange") @Validated PasswordChangeRequest password
            , BindingResult bindingResult
    , RedirectAttributes rtts) throws ProfileErrorException {
        if (bindingResult.hasErrors()) {
            rtts.addFlashAttribute("errorProfileMsg", bindingResult.getFieldError().getDefaultMessage());
            return "redirect:/account/pass-change";
        }

        UserResponse userChange = userService.updateProfilePassword(userinfo,password);

        // 비밀번호가 변경되엇을때만 로직 실행!!!!!!!!!!!!!!!!!!!!
        if(!userinfo.getPassword().equals(userChange.getPassword())){
            /* 변경된 세션 등록 */
            SecurityContextHolder.clearContext();
            // Authentication에 유저의 접근 주체 토큰을 새로 생성한다.
            Authentication newAuthentication = new UsernamePasswordAuthenticationToken(userChange, null, userChange.getAuthorities());
            // SecurityContextHolder에서 관리하는 접근주체에 새로 생성된 유저의 정보를 담아 저장한다.
            SecurityContextHolder.getContext().setAuthentication(newAuthentication);
        }
        return "pages/mypage/pass";
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
    public String passChangeGet(@ModelAttribute("userPassChange") PasswordChangeRequest userPassChange){
        return "pages/mypage/pass";
    }

    @GetMapping("tasting")
    public String tatstingList(Model model, HttpServletRequest request) {
        String referer = (String)request.getHeader("REFERER");
        if(referer.contains("/post/info/")){
            model.addAttribute("useBackPageNum", String.valueOf(referer.substring(referer.lastIndexOf("/")+1)));
        }
        model.addAttribute("PageActiveBtt",true);
        return "pages/mypage/mypostlist";
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


}
