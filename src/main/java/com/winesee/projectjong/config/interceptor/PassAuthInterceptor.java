package com.winesee.projectjong.config.interceptor;

import com.winesee.projectjong.domain.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;


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
            cookieJoinOrCreate(request, response,180,pppAtCookieValue);
            //cookie.setSecure(true);
            response.sendRedirect(request.getServletPath());
            return false;
        }

        // ppp_at 쿠키가 존재하면서 GET요청이고 쿠키값이 발행한 원본과 일치할경우에 수정 완료화면 보여줌.
        if(request.getMethod().equals("GET") && Arrays.stream(request.getCookies()).anyMatch(cookie -> cookie.getName().contains("ppp_at"))){

            // 쿠키가 존재할경우. 보여줄 화면
            if(Arrays.stream(request.getCookies()).
                            filter(cookie -> cookie.getName().contains("ppp_at"))
                            .anyMatch(cookie -> passwordEncoder.matches(user.getId().toString(),cookie.getValue()))){
                // profileSuccess 화면을 보여줌
                request.setAttribute("profileSuccess", true);
            }


//            // GET요청후 쿠키가 존재하지 않다면 ( 상황 : 유저가 변경하지 않고 다른 페이지에 있다가 돌아오는경우. )
//            if(Arrays.stream(request.getCookies()).noneMatch(cookie -> cookie.getName().contains("ppp_at"))
//                    && ObjectUtils.isNotEmpty(request.getSession().getAttribute("userProfilePasswordAuth")) ) {
//                log.info("세션 제거");
//                // 세션을 제거
//                request.getSession().removeAttribute("userProfilePasswordAuth");
//            }
        }

        // PUT요청후 쿠키가 존재하지 않는다면. 인증이 만료된것으로 간주. ( 만약 유저가 변경하지않고 다른페이지를 보다가 다시 돌아온 경우 )
        if(request.getMethod().equals("PUT") && Arrays.stream(request.getCookies()).noneMatch(cookie -> cookie.getName().contains("ppp_at"))) {
            response.sendRedirect(request.getServletPath());
            return false;
        }

        // PUT요청후 쿠키 1초로 변경. ( 변경후 변경 내용을 보여주기 위해 )
        if(request.getMethod().equals("PUT") && Arrays.stream(request.getCookies()).anyMatch(cookie -> cookie.getName().contains("ppp_at"))){
            cookieJoinOrCreate(request, response,1,"");
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
        Cookie cookie = Arrays.stream(request.getCookies())
                    .filter(c -> c.getName().contains("ppp_at"))
                    .findFirst().orElseGet(() -> new Cookie("ppp_at",cookieValue));
        cookie.setPath("/");
        cookie.setMaxAge(time);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
        return cookie;
    }

}
