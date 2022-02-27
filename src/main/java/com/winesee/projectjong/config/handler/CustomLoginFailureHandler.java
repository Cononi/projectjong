package com.winesee.projectjong.config.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        request.setAttribute("ad", "비밀번호가 틀려습니다.");
        request.getRequestDispatcher("/account/login").forward(request, response);
//        RequestDispatcher dispatcher = request.getRequestDispatcher("/account/login");
//        dispatcher.forward(request, response);
        // 로그인 페이지로 다시 포워딩
    }
}
