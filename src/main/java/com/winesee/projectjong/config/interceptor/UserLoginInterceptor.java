package com.winesee.projectjong.config.interceptor;

import com.winesee.projectjong.domain.user.dto.UserResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

import static com.winesee.projectjong.config.constant.SecurityConstant.LOGIN_USER_RESPONSE_URL;

@Slf4j
public class UserLoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 로그인 중인지 체크.
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(!authentication.getName().equals("anonymousUser")){
            // 로그인 중인유저가 register, login 페이지에 접근했을때.
            if(Arrays.stream(LOGIN_USER_RESPONSE_URL).anyMatch(url -> url.equals(request.getRequestURI()))){
                response.sendRedirect("/");
                return false;
            }
            UserResponse userinfo = (UserResponse) authentication.getPrincipal();
            request.setAttribute("userinfo",userinfo);
        }
        return true;
    }

}
