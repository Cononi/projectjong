package com.winesee.projectjong.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.winesee.projectjong.domain.user.Role;
import com.winesee.projectjong.domain.user.User;
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
public class UserInfoResponse {
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
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime modifieDate;

    /**
     * 유저 Entity를 UserResponse 클래스 데이터로 변경.
     * @param entity User Entity 클래스
     */
    public UserInfoResponse(User entity) {
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
        this.createDate = entity.getCreateDate();
        this.modifieDate = entity.getModifieDate();
    }
}
