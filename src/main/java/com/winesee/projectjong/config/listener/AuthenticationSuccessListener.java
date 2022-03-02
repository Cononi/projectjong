package com.winesee.projectjong.config.listener;

import com.winesee.projectjong.domain.user.User;
import com.winesee.projectjong.service.attempt.LoginAttemptAddressService;
import com.winesee.projectjong.service.attempt.LoginAttemptService;
import lombok.AllArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;


@Component
@AllArgsConstructor
public class AuthenticationSuccessListener {
    private LoginAttemptService loginAttempService;
    private LoginAttemptAddressService loginAttemptCacheAddressService;

    @EventListener
    public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
        WebAuthenticationDetails auth = (WebAuthenticationDetails)
                event.getAuthentication().getDetails();
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof User) {
            User user = (User) event.getAuthentication().getPrincipal();
            loginAttempService.evictUserFromLoginAttemptCache(user.getUsername());
        }
        loginAttemptCacheAddressService.evictUserFromLoginAttemptCache(auth.getRemoteAddress());
    }
}
