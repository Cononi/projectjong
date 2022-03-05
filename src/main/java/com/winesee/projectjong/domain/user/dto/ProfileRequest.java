package com.winesee.projectjong.domain.user.dto;

import com.winesee.projectjong.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author Jhong
 * @version 1.0
 * @since 2022-02-14
 * 유저 Request
 * View에 데이터를 요청받아 Entity로 변환.
 * View -> Controller -> Service -> Entity 변환 ->  Save
 */
@Getter
public class ProfileRequest {
    @NotBlank(message = "닉네임은 필수 입력 입니다.")
    @Pattern(regexp = "^[0-9a-zA-Z가-힣]{3,10}$", message = "닉네임은 3자리부터 10자리까지 영문,한글,숫자만 입력 가능합니다.")
    private String name;
    private MultipartFile profileImage;

    @Builder
    public ProfileRequest(String name, String username, String email, MultipartFile profileImage) {
        this.name = name;
        this.profileImage = profileImage;
    }

    public User toEntity() {
        return User.builder()
                .name(name)
                .build();
    }

}
