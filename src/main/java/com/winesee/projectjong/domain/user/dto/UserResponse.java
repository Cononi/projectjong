package com.winesee.projectjong.domain.user.dto;

import com.winesee.projectjong.domain.user.Role;
import com.winesee.projectjong.domain.user.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Jhong
 * @version 1.0
 * @since 2022-02-14
 * 유저 Response
 * User Entity 데이터를 받아와 view에 응답.
 * View -> Controller -> Service -> Entity 데이터 요청 -> Response 반환
 */
@Getter
public class UserResponse extends User {
    private Long id;
    private String name;
    private String username;
    private String email;
    private Boolean isActive;
    private Boolean isNonLocked;
    private Boolean isEmailEnabled;
    private LocalDateTime lastLoginDate; // 마지막 로그인 시간
    private Role roles;
    private String profileImageUrl;

    /**
     * 유저 Entity를 UserResponse 클래스 데이터로 변경.
     * @param entity User Entity 클래스
     */
    public UserResponse(User entity) {
        super(entity.getId(),entity.getName(),entity.getUsername(),entity.getPassword(),entity.getEmail()
        ,entity.getIsActive(),entity.getIsNonLocked(),entity.getIsEmailEnabled(),entity.getLastLoginDate()
        ,entity.getRoles(),entity.getProfileImageUrl());
        this.id = entity.getId();
        this.name = entity.getName();
        this.username = entity.getUsername();
        this.email = entity.getEmail();
        this.isActive = entity.getIsActive();
        this.isNonLocked = entity.getIsNonLocked();
        this.isEmailEnabled = entity.getIsEmailEnabled();
        this.lastLoginDate = entity.getLastLoginDate();
        this.roles = entity.getRoles();
        this.profileImageUrl = entity.getProfileImageUrl();
    }
}
