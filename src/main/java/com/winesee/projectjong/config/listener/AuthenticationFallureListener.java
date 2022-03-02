package com.winesee.projectjong.config.listener;

import com.winesee.projectjong.service.attempt.LoginAttemptAddressService;
import com.winesee.projectjong.service.attempt.LoginAttemptService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
@AllArgsConstructor
@Slf4j
public class AuthenticationFallureListener {

    private final LoginAttemptService loginAttempService;
    private LoginAttemptAddressService loginAttemptCacheAddressService;

    @EventListener
    public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) throws ExecutionException {
        WebAuthenticationDetails auth = (WebAuthenticationDetails)
                event.getAuthentication().getDetails();
        Object principal = event.getAuthentication().getPrincipal();
        if(principal instanceof String) {
            String username = (String) event.getAuthentication().getPrincipal();
            loginAttempService.addUserToLoginAttemptCache(username);
        }
        loginAttemptCacheAddressService.addUserToLoginAttemptCache(auth.getRemoteAddress());
    }
}
