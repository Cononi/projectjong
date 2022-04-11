package com.winesee.projectjong.config.constant;

public class SecurityConstant {

//     권한 없이 누구나 접근가능한 주소들
    public static final String[] PUBLIC_URLS = { "/","/api/find/**","/assets/**","/api/image/**", "/api/v1/post/**/list/**","/api/v1/notice/**"
    , "/account/login", "/account/register", "/account/message", "/account/email/sign/**", "/wine/**", "/post/info/**","/js/**", "/image/wine/**", "/api/v1/comment/**","/notice/**","/notice"};
    public static final String[] ADMIN_URLS = { "/api/v1/notice/image/upload", "/notice/post", "/api/v1/notice/post"};
    public static final String[] LOGIN_USER_RESPONSE_URL = { "/account/login", "/account/register"};
//    public static final String[] PUBLIC_URLS = { "**" };
}