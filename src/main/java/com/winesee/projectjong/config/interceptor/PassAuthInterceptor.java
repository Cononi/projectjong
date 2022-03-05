package com.winesee.projectjong.config.interceptor;

import com.winesee.projectjong.config.util.CookieUtil;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import com.winesee.projectjong.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

import static com.winesee.projectjong.config.constant.SecurityConstant.LOGIN_USER_RESPONSE_URL;

@Slf4j

/**
 * 기본적으로 전체페이지에서 Login후 UserResponse를 반환하여 유저정보를 기입하기 위한 인터셉터.
 * 부가적 자원 : /account/login, register
 */

@RequiredArgsConstructor
public class PassAuthInterceptor implements HandlerInterceptor {

    private final PasswordEncoder passwordEncoder;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 로그인한 유저의 정보를 가져옴.
        UserResponse user = (UserResponse) request.getAttribute("userinfo");
        // post 요청이면서 암호가 통과했을때 인증 쿠키 생성
        if(request.getMethod().equals("POST") &&
        passwordEncoder.matches(request.getParameter("passwordAuth"),user.getPassword())){
            // 쿠키값 암호화
            String pppAtCookieValue = passwordEncoder.encode(user.getId().toString());
            // ppw_at (profile password page auth )
            Cookie cookie = new Cookie("ppp_at", pppAtCookieValue);
            // 쿠키 만료 시간 5분
            cookie.setMaxAge(300);
            cookie.setHttpOnly(true);
            // 차후 HTTPS를 쓴다면
            //cookie.setSecure(true);
            response.addCookie(cookie);
            response.sendRedirect(request.getServletPath());
            // 비밀번호 세션 저장.
            request.getSession().setAttribute("userProfilePasswordAuth",request.getParameter("passwordAuth"));
            return false;
        }

        // ppp_at 쿠키가 존재하면서 GET요청이고 쿠키값이 발행한 원본과 일치할경우에 수정 완료화면 보여줌.
        if(Arrays.stream(request.getCookies()).anyMatch(cookie -> cookie.getName().contains("ppp_at"))){
            log.info("오우");
            if(request.getMethod().equals("GET") &&
                    Arrays.stream(request.getCookies()).
                            anyMatch(cookie -> passwordEncoder.matches(user.getId().toString(),cookie.getValue()))){
                // profileSuccess 화면을 보여줌
                request.setAttribute("profileSuccess", true);
            }
            // 쿠키가 존재하지 않을경우 세션도 삭제.
        } else if(Arrays.stream(request.getCookies()).noneMatch(cookie -> cookie.getName().contains("ppp_at"))
                && ObjectUtils.isNotEmpty(request.getSession().getAttribute("userProfilePasswordAuth"))){
            log.info("세션 삭제");
            request.getSession().removeAttribute("userProfilePasswordAuth");
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // put 요청으로 수정이 완료되었을때.

//        log.info(String.valueOf(response.getStatus()));
//        if(request.getMethod().equals("PUT")) {
//            Cookie cookie = Arrays.stream(request.getCookies())
//                    .filter(c -> c.getName().equals("ppp_at"))
//                    .findAny()
//                    .orElseThrow(IllegalArgumentException::new);
//            cookie.setMaxAge(0);
//            response.addCookie(cookie);
//            request.getSession().removeAttribute("userProfilePasswordAuth");
//        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        HandlerInterceptor.super.afterCompletion(request, response, handler, ex);
    }
}
