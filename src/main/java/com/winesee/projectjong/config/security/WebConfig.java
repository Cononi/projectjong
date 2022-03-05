package com.winesee.projectjong.config.security;

import com.winesee.projectjong.config.interceptor.PassAuthInterceptor;
import com.winesee.projectjong.config.interceptor.UserLoginInterceptor;
import com.winesee.projectjong.config.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final  PasswordEncoder passwordEncoder;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/assets/**", "/api/**");
        registry.addInterceptor(new PassAuthInterceptor(passwordEncoder))
                .addPathPatterns("/account/mypage", "/account/pass-change")
                .excludePathPatterns("/assets/**", "/api/**");
    }

    @Bean
    public UserLoginInterceptor userInterceptor() {
        return new UserLoginInterceptor();
    }


}
