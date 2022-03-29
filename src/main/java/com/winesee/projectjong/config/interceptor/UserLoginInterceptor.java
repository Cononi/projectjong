package com.winesee.projectjong.config.interceptor;

import com.winesee.projectjong.config.util.CookieUtil;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import com.winesee.projectjong.domain.wine.Search;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.ObjectUtils;
import org.springframework.web.servlet.HandlerInterceptor;

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
public class UserLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 로그인 중인지 체크.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!ObjectUtils.isEmpty(authentication) && !authentication.getName().equals("anonymousUser")){
            // 로그인 중인유저가 register, login 페이지에 접근했을때.
            if(Arrays.stream(LOGIN_USER_RESPONSE_URL).anyMatch(url -> url.equals(request.getRequestURI()))){
                response.sendRedirect("/");
                return false;
            }
            UserResponse userinfo = (UserResponse) authentication.getPrincipal();
            request.setAttribute("userinfo",userinfo);
            request.getSession().removeAttribute(authentication.getName());
        }
        return true;
    }

}
