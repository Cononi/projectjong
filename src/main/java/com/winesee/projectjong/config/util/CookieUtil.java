package com.winesee.projectjong.config.util;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Service
public class CookieUtil {

    public Cookie createCookie(String cookieName, String value, String path){
        Cookie token = new Cookie(cookieName,value);
        token.setHttpOnly(true);
        token.setSecure(false);
        token.setMaxAge(24*60*60);
        token.setPath(path);
        return token;
    }

    public Cookie getCookie(HttpServletRequest req, String cookieName){
        final Cookie[] cookies = req.getCookies();
        if(cookies==null) return null;
        for(Cookie cookie : cookies){
            if(cookie.getName().equals(cookieName))
                return cookie;
        }
        return null;
    }

}