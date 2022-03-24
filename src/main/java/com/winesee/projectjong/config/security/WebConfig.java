package com.winesee.projectjong.config.security;

import com.winesee.projectjong.config.interceptor.PassAuthInterceptor;
import com.winesee.projectjong.config.interceptor.UserLoginInterceptor;
import com.winesee.projectjong.config.util.CookieUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
@EnableWebMvc
public class WebConfig implements WebMvcConfigurer {

    private final  PasswordEncoder passwordEncoder;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/assets/**", "/api/**","/js/**");
        registry.addInterceptor(new PassAuthInterceptor(passwordEncoder))
                .addPathPatterns("/account/mypage", "/account/pass-change")
                .excludePathPatterns("/assets/**", "/api/**","/js/**");
    }


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        CacheControl cacheControl = CacheControl.maxAge(5, TimeUnit.SECONDS);
        CacheControl cacheControl2 = CacheControl.maxAge(30, TimeUnit.DAYS).cachePublic();

        registry.addResourceHandler("/assets/**")
                .addResourceLocations("classpath:/static/assets/")
                .setCacheControl(cacheControl2);

        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCacheControl(cacheControl);
    }

    @Bean
    public UserLoginInterceptor userInterceptor() {
        return new UserLoginInterceptor();
    }


}
