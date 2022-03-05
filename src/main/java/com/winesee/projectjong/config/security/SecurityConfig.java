package com.winesee.projectjong.config.security;

import com.winesee.projectjong.config.handler.CustomLoginFailureHandler;
import com.winesee.projectjong.config.handler.CustomLoginSuccessHandler;
import com.winesee.projectjong.service.UserService;
import com.winesee.projectjong.service.attempt.LoginAttemptAddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.web.filter.HiddenHttpMethodFilter;

import static com.winesee.projectjong.config.constant.SecurityConstant.*;

/**
 * @author Jhong
 * @version 1.0
 * @since 2022-02-14
 * 스프링 Security 설정.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    // 사용자 세부 정보 서비스를 생성 ( 인더페이스 상속받아서 구현. )
    private final UserDetailsService userDetailsService;
    // 암호 인코더를 사용.
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final LoginAttemptAddressService loginAttemptAddressService;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .formLogin()
                    .loginPage("/account/login")
                    .failureUrl("/account/login")
                    .usernameParameter("username")
                    .passwordParameter("password")
                    .loginProcessingUrl("/account/login")
                    .successHandler(successHandler())
                    .failureHandler(failureHandler());
        http
                .logout()
                    .logoutUrl("/account/logout")
                    .logoutSuccessUrl("/")
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID", "ppp_at");

        http
                .csrf().disable()
                // 모든 사용자가 해당 메뉴를 제외하고 액세스를 허용함
                .authorizeRequests().antMatchers(PUBLIC_URLS).permitAll()
                .and()
                .authorizeRequests().antMatchers("/api/delete/**").hasRole("ADMIN")
                .anyRequest().authenticated()
                .and()
                .sessionManagement()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(false)
                .expiredUrl("/account/login")
                .sessionRegistry(sessionRegistry());


    }


    // logout 후 login할 때 정상동작을 위함
    @Bean
    public SessionRegistry sessionRegistry() {
        return new SessionRegistryImpl();
    }

    // 로그인 성공시
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return new CustomLoginSuccessHandler("/");
    }

    // 로그인 실패시
    @Bean
    public CustomLoginFailureHandler failureHandler() {
        return new CustomLoginFailureHandler(loginAttemptAddressService);
    }

    // 회원 정보 수정시
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // form에서 put delete 메소드를 받기위해
    @Bean
    public HiddenHttpMethodFilter httpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }
}
