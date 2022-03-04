package com.winesee.projectjong.config.security;

import com.winesee.projectjong.config.interceptor.UserLoginInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(userInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/assets/**", "/api/**");
    }

    @Bean
    public UserLoginInterceptor userInterceptor() {
        return new UserLoginInterceptor();
    }


}
