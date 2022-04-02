package com.winesee.projectjong.domain.user.dto;

import com.winesee.projectjong.domain.user.Role;
import com.winesee.projectjong.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalDateTime;

/**
 * @author Jhong
 * @version 1.0
 * @since 2022-02-14
 * 유저 Request
 * View에 데이터를 요청받아 Entity로 변환.
 * View -> Controller -> Service -> Entity 변환 ->  Save
 */
@Getter
public class UserRequest {
    @NotBlank(message = "닉네임은 필수 입력 입니다.")
    @Pattern(regexp = "^[0-9a-zA-Z가-힣]{1,10}$", message = "닉네임은 1자리부터 10자리까지 영문,한글,숫자만 입력 가능합니다.")
    private String name;
    @NotBlank(message = "아이디는 필수 입력 입니다.")
    @Pattern(regexp = "^[A-Za-z]{1}[A-Za-z0-9]{4,19}$", message = "아이디는 소문자 영문시작 그리고 최소 5자리 이상 19자 이하 영문과 숫자만 입력 가능합니다.")
    private String username;
    @NotBlank(message = "비밀번호는 필수 입력입니다.")
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[!@#$%^*+=-])(?=.*[0-9]).{8,25}$", message="비밀번호는 8자 이상이어야 하며, 숫자/영문자/특수문자를 모두 포함해야 합니다.")
    private String password;
    @NotBlank(message = "이메일은 필수 입력입니다.")
    @Email(message="이메일 형식에 맞게 입력해주시기 바랍니다.")
    private String email;
    private MultipartFile profileImage;

    @Builder
    public UserRequest(String name, String username, String password, String email, MultipartFile profileImage) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.email = email;
        this.profileImage = profileImage;
    }

    public User toEntity() {
        return User.builder()
                .name(name)
                .username(username)
                .password(password)
                .email(email)
                .build();
    }

}
