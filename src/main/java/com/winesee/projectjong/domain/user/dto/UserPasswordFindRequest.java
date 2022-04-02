package com.winesee.projectjong.domain.user.dto;

import com.winesee.projectjong.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

/**
 * @author Jhong
 * @version 1.0
 * @since 2022-02-14
 * 유저 Request
 * View에 데이터를 요청받아 Entity로 변환.
 * View -> Controller -> Service -> Entity 변환 ->  Save
 */
@Getter
@NoArgsConstructor
public class UserPasswordFindRequest {
    @NotBlank(message = "아이디는 필수 입력 입니다.")
    @Pattern(regexp = "^[A-Za-z]{1}[A-Za-z0-9]{4,19}$", message = "아이디는 소문자 영문시작 그리고 최소 5자리 이상 19자 이하 영문과 숫자만 입력 가능합니다.")
    private String username;
    @NotBlank(message = "이메일은 필수 입력입니다.")
    @Email(message="이메일 형식에 맞게 입력해주시기 바랍니다.")
    private String email;


}
