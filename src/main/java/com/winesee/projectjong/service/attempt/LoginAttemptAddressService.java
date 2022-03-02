package com.winesee.projectjong.service.attempt;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

import static java.util.concurrent.TimeUnit.HOURS;
import static java.util.concurrent.TimeUnit.MINUTES;

@Service
@Slf4j
public class LoginAttemptAddressService {
    private static final int MAXIMUM_NUMBER_OF_ATTEMPTSADRESS = 3;
    private static final int ATTEMPT_INCREMENT = 1;
    private final LoadingCache<String, Integer> loginAttemptCacheAdress;

    public LoginAttemptAddressService() {
        super();
        // 접근 차단
        loginAttemptCacheAdress = CacheBuilder.newBuilder()
                .expireAfterWrite(1, HOURS)
                .maximumSize(100)
                .build(new CacheLoader<String, Integer>() {
                    public Integer load(String key){
                        return 0;
                    }
                });
    }

    public void evictUserFromLoginAttemptCache(String address) {
        loginAttemptCacheAdress.invalidate(address);
    }

    public void addUserToLoginAttemptCache(String address){
        int attemptAddress = 0;
        try {
            attemptAddress = ATTEMPT_INCREMENT + loginAttemptCacheAdress.get(address);
            log.info(String.valueOf(attemptAddress));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        loginAttemptCacheAdress.put(address, attemptAddress);
    }

    public boolean hasExceededMaxAttemptAddress(String address) {
        try {
            return loginAttemptCacheAdress.get(address) >= MAXIMUM_NUMBER_OF_ATTEMPTSADRESS;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
