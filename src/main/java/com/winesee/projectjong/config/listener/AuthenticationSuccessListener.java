package com.winesee.projectjong.config.listener;

import com.winesee.projectjong.domain.user.User;
import com.winesee.projectjong.service.attempt.LoginAttemptService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class AuthenticationSuccessListener {
    private LoginAttemptService loginAttempService;

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof User) {
            User user = (User) event.getAuthentication().getPrincipal();
            loginAttempService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }
}
