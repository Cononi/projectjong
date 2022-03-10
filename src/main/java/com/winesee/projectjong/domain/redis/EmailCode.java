package com.winesee.projectjong.domain.redis;

import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.time.LocalDateTime;

@Getter
@RedisHash(value = "emailCode", timeToLive = 3600) // 1시간
public class EmailCode {

    @Id
    private String id;
    private String email;
    private String code;
    private LocalDateTime createdAt;

    public EmailCode(String email) {
        this.email = email;
        this.code = codeRandom();
        this.createdAt = LocalDateTime.now();
    }

    public String codeRandom() {
        return RandomStringUtils.randomNumeric(6);
    }
}
