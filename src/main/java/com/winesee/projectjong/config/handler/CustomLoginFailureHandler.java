package com.winesee.projectjong.config.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
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
        request.setAttribute("errorExceptionMSG", errorExceptionMessage(exception));
        // 로그인 페이지로 다시 포워딩
        request.getRequestDispatcher("/account/login").forward(request, response);
    }

    private String errorExceptionMessage(AuthenticationException exception) {
//        if (exception instanceof AccountExpiredException) {
//            return "계정이 만료되었습니다.";
//        if (exception instanceof CredentialsExpiredException) {
//            return "비밀번호만료";
        if (exception instanceof DisabledException) {
            return "계정이 차단되었습니다. 관리자에게 문의 바랍니다.";
        }else if (exception instanceof LockedException) {
            return "최대 입력횟수 5회 초과하여 정지되었습니다. 아이디 또는 비밀번호 찾기를 진행해 주시기 바랍니다.";
        } else {
            return "아이디 또는 비밀번호를 잘못 입력했습니다.\n입력하신 내용을 다시 확인해주세요. ";
        }
    }
}
