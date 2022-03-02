package com.winesee.projectjong.config.handler;

import com.winesee.projectjong.service.attempt.LoginAttemptAddressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.thymeleaf.util.TextUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class CustomLoginFailureHandler implements AuthenticationFailureHandler {

    private final LoginAttemptAddressService loginAttemptAddressService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        request.setAttribute("errorExceptionMSG", errorExceptionMessage(exception,request));
        // 로그인 페이지로 다시 포워딩
        request.getRequestDispatcher("/account/login").forward(request, response);
    }

    private String errorExceptionMessage(AuthenticationException exception, HttpServletRequest request) {
        if(loginAttemptAddressService.hasExceededMaxAttemptAddress(getClientIP(request)))
            return "로그인이 차단되었습니다. 일정시간동안 로그인이 불가능합니다.";
//        if (exception instanceof AccountExpiredException) {
//            return "계정이 만료되었습니다.";
//        if (exception instanceof CredentialsExpiredException) {
//            return "비밀번호만료";
        if (exception instanceof DisabledException) {
            return "이메일 인증이 안되었습니다. 이메일 인증을 받아주세요.";
        }else if (exception instanceof LockedException) {
            return "최대 입력횟수 5회 초과하여 계정이 잠겼습니다. 아이디 또는 비밀번호 찾기를 진행해 주시기 바랍니다.";
        } else if (exception instanceof AccountExpiredException){
            return "해당 계정은 정지상태 입니다. 관리자에게 문의바랍니다.";
        } else {
            return "아이디 또는 비밀번호를 잘못 입력했습니다.\n입력하신 내용을 다시 확인해주세요. ";
        }
    }
    private String getClientIP(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");

        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("x-real-ip");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("x-original-forwarded-for");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_X_FORWARDED");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_X_CLUSTER_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_FORWARDED_FOR");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_FORWARDED");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("HTTP_VIA");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getHeader("REMOTE_ADDR");
        }
        if (ip == null || ip.length() == 0 || ip.equalsIgnoreCase("unknown")) {
            ip = request.getRemoteAddr();
        } else {
            return ip.split(",")[0];
        }

        return ip;
    }
}
