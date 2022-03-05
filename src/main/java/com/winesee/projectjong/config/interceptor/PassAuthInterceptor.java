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
import java.util.Enumeration;

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
        // 로그인한 유저의 정보
        UserResponse user = (UserResponse) request.getAttribute("userinfo");

        // post 요청이면서 암호가 통과했을때 인증 쿠키 생성
        if(request.getMethod().equals("POST") &&
        passwordEncoder.matches(request.getParameter("passwordAuth"),user.getPassword())){
            // 쿠키값 암호화
            String pppAtCookieValue = passwordEncoder.encode(user.getId().toString());
            // 쿠키 생성.
            Cookie cookie = cookieJoinOrCreate(request, response,180,pppAtCookieValue);
            // 차후 HTTPS를 쓴다면
            //cookie.setSecure(true);
            response.sendRedirect(request.getServletPath());
            // 비밀번호 세션 저장.
            request.getSession().setAttribute("userProfilePasswordAuth",request.getParameter("passwordAuth"));
            return false;
        }

        // ppp_at 쿠키가 존재하면서 GET요청이고 쿠키값이 발행한 원본과 일치할경우에 수정 완료화면 보여줌.
        if(request.getMethod().equals("GET") && Arrays.stream(request.getCookies()).anyMatch(cookie -> cookie.getName().contains("ppp_at"))){
            // 수정 필요 (Encoded password does not look like BCrypt) 메세지 나옴
            if(Arrays.stream(request.getCookies()).
                            anyMatch(cookie -> passwordEncoder.matches(user.getId().toString(),cookie.getValue()))){
                // profileSuccess 화면을 보여줌
                request.setAttribute("profileSuccess", true);
            }
            // 세션이 존재하지 않으면 쿠키 삭제. (세션이 사라졌을 경우)
            if(ObjectUtils.isEmpty(request.getSession().getAttribute("userProfilePasswordAuth"))){
                // 쿠키 조회 및 삭제.
                Cookie cookie = cookieJoinOrCreate(request, response,0, "");
                response.sendRedirect(request.getServletPath());
                return false;
                // 쿠키가 존재하지 않을경우. (세션이 존재하지만 쿠키가 없을경우 )
            } else if(Arrays.stream(request.getCookies())
                    .noneMatch(cookie -> cookie.getName().equals("ppp_at"))){
                // 세션 삭제
                request.getSession().removeAttribute("userProfilePasswordAuth");
            }
        }

        // put시 쿠키를 1초주면서 수정 내용을 확인하도록 함.
        if(request.getMethod().equals("PUT")){
            Cookie cookie = cookieJoinOrCreate(request, response, 1,"");
        }
        // 링크 버튼 class active 활성화
        request.setAttribute("thisMypageActive", request.getServletPath());
        return true;
    }


    /**
     * 쿠키 조회를 위한 메소드
     * @param request HttpServletRequest - 서블릿 요청
     * @param time int - 쿠키 시간
     * @param cookieValue String - 쿠키값
     * @return Cookie
     */
    public Cookie cookieJoinOrCreate(HttpServletRequest request, HttpServletResponse response, int time, String cookieValue){
        // ppw_at (profile password page auth )
        Cookie cookie = StringUtils.isBlank(cookieValue)
                // true
                ? Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().contains("ppp_at"))
                    .findAny().orElseThrow(() -> new IllegalArgumentException("존재하지 않는 쿠키."))
                // false
                : new Cookie("ppp_at",cookieValue);
        cookie.setPath("/");
        cookie.setMaxAge(time);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return cookie;
    }
}
