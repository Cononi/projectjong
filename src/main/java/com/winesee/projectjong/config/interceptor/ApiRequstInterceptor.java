package com.winesee.projectjong.config.interceptor;

import com.winesee.projectjong.domain.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

import static com.winesee.projectjong.config.constant.SecurityConstant.LOGIN_USER_RESPONSE_URL;

@Slf4j

/**
 * 기본적으로 전체페이지에서 Login후 UserResponse를 반환하여 유저정보를 기입하기 위한 인터셉터.
 * 부가적 자원 : /account/login, register
 */
@RequiredArgsConstructor
public class ApiRequstInterceptor implements HandlerInterceptor {

    private final PasswordEncoder passwordEncoder;

//    @Override
//    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
//        log.info("여기로 돌어 오는가?");
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//        Cookie cookie = Arrays.stream(request.getCookies())
//                .filter(c -> c.getName().contains("chk-po"))
//                .findFirst().orElseGet(() -> new Cookie("chk-po",passwordEncoder.encode(authentication.getName()+1)));
//        cookie.setPath("/");
//        response.addCookie(cookie);
//    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 세션 생성.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        request.getSession().setAttribute(authentication.getName(),true);
        return true;
    }
}
