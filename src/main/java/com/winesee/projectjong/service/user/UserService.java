package com.winesee.projectjong.service.user;

import com.winesee.projectjong.config.exception.*;
import com.winesee.projectjong.domain.user.Role;
import com.winesee.projectjong.domain.user.User;
import com.winesee.projectjong.domain.user.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
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
    void register(UserRequest userRequest) throws IOException, UserNotFoundException, EmailExistException, UsernameExistException, MessagingException;

    /**
     * 유저 가져오기
     * @param username 유저 ID
     * @return UserResponse 반환
     */
    UserResponse getUser(String username);

    /**
     * 유저 가져오기
     * @param username 유저 ID
     * @return UserResponse 반환
     */
    UserResponse getUserIdResponse(Long username) throws UserNotFoundException;

    /**
     * 유저 가져오기
     * @param id 유저 ID
     * @return UserResponse 반환
     */
    UserInfoResponse getUserId(Long id) throws UserNotFoundException;

    /**
     * 모든 유저 가져오기
     * @return List<UserResponse>를 반환
     */
    Page<UserInfoResponse> getUsers(int page);

    /**
     * 이름으로 회원 정보 조회
     * @param username String
     * @return User.
     */
    User findByUsername(String username);

    /**
     * 이메일로 회원 정보 조회
     * @param email String
     * @return User
     */
    User findByEmail(String email);

    /**
     * 이름으로 회원 정보 조회
     * @param name String
     * @return User
     */
    User findByName(String name);
    /**
     * 유저 프로필 이미지 업데이트
     * @param name String
     * @param profileImage MultipartFile ( jpg타입 )
     * @return UserResponse
     * @throws IOException IO관련 익셉션
     */
    UserResponse updateProfile(UserResponse userinfo, String name,MultipartFile profileImage) throws IOException, NotAnImageFileException, ProfileErrorException;

    /**
     * 유저 삭제
     * @param id long
     */
    void deleteUser(long id);

//    /**
//     * 비밀번호 초기화
//     * @param email String
//     * @throws MessagingException 메세지 익셉션
//     * @throws EmailNotFoundException 이메일 익셉션
//     */

    UserResponse updateProfilePassword(UserResponse userinfo, PasswordChangeRequest request) throws ProfileErrorException;

    String userCheck(String username, String uri) throws UserNotFoundException, EmailExistException, UsernameExistException;

    // 보류 JS에서 처리 가능함.
    Errors userValidateCheck(Errors errors, UserRequest user);

    List<String> emailConfirm(String email, String authId, String authKey) throws MessagingException;

    String userPasswordFind(UserPasswordFindRequest request);

    boolean passwordAuth(String password, String passwordAuth);

    // 어드민 페이지에서  회원 정보 수정

    void adminPageUserEdit(UserAdminEditRequest request, Long userId);

}


