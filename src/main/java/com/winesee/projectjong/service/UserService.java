package com.winesee.projectjong.service;

import com.winesee.projectjong.domain.user.Role;
import com.winesee.projectjong.domain.user.User;
import com.winesee.projectjong.domain.user.dto.UserRequest;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import org.springframework.web.multipart.MultipartFile;

import javax.mail.MessagingException;
import java.io.IOException;
import java.util.List;

/**
 * 유저 서비스 관련 인터페이스 정의
 * @author Jhong
 * @version 1.0
 * @since 2022-02-14
 */
public interface UserService {
    /**
     * 유저 화원가입.
     * @param userRequest UserRequest 데이터.
     * @return UserResponse 데이터
     */
    UserResponse regiter(UserRequest userRequest) throws IOException;

    /**
     * 유저 가져오기
     * @param username 유저 ID
     * @return UserResponse 반환
     */
    UserResponse getUser(String username);

    /**
     * 모든 유저 가져오기
     * @return List<UserResponse>를 반환
     */
    List<UserResponse> getUsers();

    /**
     * 이름으로 회원 정보 조회
     * @param username String
     * @return User.
     */
    /*-----------------------------------------------
    findByUsername - 유저 이름으로 정보 조회
    -----------------------------------------------*/
    UserResponse findByUsername(String username);

    /**
     * 이메일로 회원 정보 조회
     * @param email String
     * @return User
     */
    User findUserByEmail(String email);

    /**
     * 유저 프로필 이미지 업데이트
     * @param username String
     * @param profileImage MultipartFile ( jpg타입 )
     * @return UserResponse
     * @throws IOException IO관련 익셉션
     */
//    UserResponse updateProfileImage(String username, MultipartFile profileImage) throws IOException;

    /**
     * 유저 삭제
     * @param id long
     */
    void deleteUser(long id);

    /**
     * 비밀번호 초기화
     * @param email String
     * @throws MessagingException 메세지 익셉션
     * @throws EmailNotFoundException 이메일 익셉션
     */
}


