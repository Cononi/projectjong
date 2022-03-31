package com.winesee.projectjong.config.security;

import com.winesee.projectjong.config.interceptor.ApiRequstInterceptor;
import com.winesee.projectjong.config.interceptor.PassAuthInterceptor;
import com.winesee.projectjong.config.interceptor.UserLoginInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.TimeUnit;


@Configuration
@RequiredArgsConstructor
@EnableWebMvc
@Slf4j
public class WebConfig implements WebMvcConfigurer {


    private static final String[] nonPath = { "/assets/**", "/js/**", "/api/**"};
    private static final String[] okPath = {  "/post/**" };
    @Value("${project.base-img}")
    private String imglink;

    private final  PasswordEncoder passwordEncoder;
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/assets/**", "/api/**","/js/**")
                .order(0);
        registry.addInterceptor(new PassAuthInterceptor(passwordEncoder))
                .addPathPatterns("/account/mypage", "/account/pass-change")
                .excludePathPatterns("/assets/**", "/api/**","/js/**")
                .order(1);
        registry.addInterceptor(new ApiRequstInterceptor(passwordEncoder))
                .addPathPatterns(okPath)
                .excludePathPatterns(nonPath)
                .order(2);

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
        registry.addResourceHandler("/image/wine/**")
                .addResourceLocations(imglink)
                .setCacheControl(cacheControl2);
    }

    @Bean
    public UserLoginInterceptor userInterceptor() {

        log.info(imglink);
        return new UserLoginInterceptor();
    }


}
