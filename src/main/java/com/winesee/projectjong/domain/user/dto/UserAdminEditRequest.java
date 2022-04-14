package com.winesee.projectjong.domain.user.dto;

import com.winesee.projectjong.domain.user.Role;
import com.winesee.projectjong.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

/**
 * @author Jhong
 * @version 1.0
 * @since 2022-02-14
 * 유저 Response
 * User Entity 데이터를 받아와 view에 응답.
 * View -> Controller -> Service -> Entity 데이터 요청 -> Response 반환
 */
@Getter
public class UserAdminEditRequest{
    private String name;
    private String email;
    private Boolean isActive;
    private Boolean isNonLocked;
    private Boolean isEmailEnabled;
    private Role roles;

    @Builder
    public UserAdminEditRequest(String name, String email, Boolean isActive, Boolean isNonLocked, Boolean isEmailEnabled, Role roles) {
        this.name = name;
        this.email = email;
        this.isActive = isActive != null && isActive;
        this.isNonLocked = isNonLocked != null && isNonLocked;
        this.isEmailEnabled = isEmailEnabled != null && isEmailEnabled;
        this.roles = roles;
    }
}
