package com.winesee.projectjong.service;

import com.winesee.projectjong.config.exception.NotAnImageFileException;
import com.winesee.projectjong.domain.user.Role;
import com.winesee.projectjong.domain.user.User;
import com.winesee.projectjong.domain.user.UserRepository;
import com.winesee.projectjong.domain.user.dto.UserRequest;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.winesee.projectjong.config.constant.FileConstant.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.MediaType.*;

@Service
@RequiredArgsConstructor
@Transactional
@Qualifier("userDetailsService")
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {

    // User CRUD 인터페이스 JpaRepsitory 객체
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /*
    -    인증 및 권한 부여와 그것의 인증절차
    Authentication(인증) 사용자가 누구인지 확인합니다.
    -- Method : Login Form, HTTP authentication, Custom auth. method 제공
    --------------------------------------------------------------------------------------------------------
    중요한 개념은 인증 및 권한 부여의 개념입니다. 그렇다면 인증이란 정확히 무엇이며 권한 부여란 무엇인가?
    귀하가 누구인지 확인하는 것입니다. 따라서 애플리케이션에 액세스하려면 이메일이나 사용자 이름 및 비밀번호를 제공해야 합니다.
    그래서 이것 당신이 누구인지 확인하는 것입니다. 그래서 일어날 일은 아마도 과거의 어느 시점에서
    이 애플리케이션이나 이 웹사이트나 다른 것에 가입하고 데이터베이스에 들어가서 앱에서 실제로 요청정보에 있는 사람이 맞는지 확인하게 된다.
    만약 Jeon인 사용자인 경우 사용자 이름은 jeon이고 암호를 제공하며, 서버는 실제로 그 정보를 가지고 접속자가 jeon인지 확인하게 됩니다.
    왜냐하면 사용자는 정확한 정보나 어떤 정호가한 자격 증명을 제공하기 떄문입니다.

    -   인증 및 권한 부여와 그것의 권한 부여절차
    Authorization(권한 부여) 리소스에 액세스할 수 있는 권한이 있는지 결정
    -------------------------------------------------------------------------------------------------------
    권한 부여는는 모든 사용자가 애플리케이션 액세스할 수 있지만 리소스에 대해 동일한 액세스 권한을 갖지 않는다는 것입니다.
    권한이 있고 간단한 사용자가 있다고 가정해보고 그들은 관리자가 있습니다. 권한 부여에 따라서 관리자는 모든 응용 프로그램에 액세스할 수 있습니다.
    따라서 모든 사람이 수행해야하는 모든 작업을 수행하기 위해 애플리케이션에 액세스할수 수 있어야 하기 때문에 모두 애플리케이션으로 인증할 수 있습니다.
    그러나 그들은 같은 권한을 가지지는 않습니다. 또는 동일한 리소스에 액세스 할 수 있습니다.
    사용자는 모두 같은 애플리케이션을 사용하지만 관리자가 액세스하면 애플리케이션이 다르게 보이는 것을 볼 수 있습니다.
    승인이 수행되는 다른 방법을 볼 수 있습니다.
    Method : Access Control URLs, Access Control List (ACLs)

    따라서 특정 인증을 통해 서버의 특정 리소스에 액세스할 수 있는지 권한이 결정됩니다. 그리고 좋은예를 들면 건물 입니다.
    건물 안으로 들어가도 될까요? 인증입니다.
    따라서 건물에 들어갈 수는 있지만 특정 방에 액세스 할 수 있습니까?
    아니면 특정 층에 액세스 할 수 있습니까? 승인입니다.
     */
    /**
     * 사용자 이름을 전달하기만 하면 사용자 저장소를 사용하여 사용자를 로드할 수 있다.
     * @param username 사용자 이름
     * @return UserDetails 메서드
     * @throws UsernameNotFoundException 유저가 존재하지 않을때 Excepiton 예외발생
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 아이디."));
//            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
//            authorities.add(new SimpleGrantedAuthority(user.getRoleKey()));
        return user;
    }

    /*-----------------------------------------------
        Regiser - 회원 가입
         -----------------------------------------------*/
    @Override
    public UserResponse regiter(UserRequest userRequest) throws IOException {
        User user = User.builder()
                .name(userRequest.getName())
                .username(userRequest.getUsername())
                .password(encodePassword(userRequest.getPassword()))
                .email(userRequest.getEmail())
                .isActive(true)
                .isNonLocked(true)
                .isEmailEnabled(true)
                .lastLoginDate(null)
                .roles(Role.USER)
                .profileImageUrl(getTemporaryProfileImageUrl(userRequest.getUsername()))
                .build();
        return new UserResponse(userRepository.save(user));
    }

    /*-----------------------------------------------
    getUser - 해당 유저 정보 조회
     -----------------------------------------------*/
    @Override
    public UserResponse getUser(String username) {
        return new UserResponse(userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("해당 유저는 존재하지 않습니다.")));
    }

    /*-----------------------------------------------
    getUsers - 모든 유저 목록
     -----------------------------------------------*/
    @Override
    public List<UserResponse> getUsers() {
        List<User> user = userRepository.findAll();
        List<UserResponse> userResponse = new ArrayList<>();
        for(User response : user) {
            userResponse.add(new UserResponse(response));
        }
        return userResponse;
    }

//    /*-----------------------------------------------
//    updateProfileImage - 유저 프로필 이미지 업데이트.
//    -----------------------------------------------*/
//    @Override
//    public UserResponse updateProfileImage(String username, MultipartFile profileImage) throws IOException {
//        User user = validateNewUsernameAndEmail(username, null, null);
//        saveProfileImage(user, profileImage);
//        return new UserResponse(user);
//    }


    /*-----------------------------------------------
    findUserbyUsername - 유저 이름으로 정보 조회
    -----------------------------------------------*/
    @Override
    public UserResponse findByUsername(String username) {
        User uesr = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("존재하지 않는 유저"));
        return new UserResponse(uesr);
    }

    /*-----------------------------------------------
      findUserByEmail - 유저 이메일로 정보 조회
      -----------------------------------------------*/
    @Override
    public User findUserByEmail(String email) {
        return userRepository.findUserByEmail(email);
    }

    /*-----------------------------------------------
      deleteUser - 유저 삭제
    -----------------------------------------------*/
    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }




    /*
    #----------------------------------------------------------------------------------------
    Sub - Method.
    #----------------------------------------------------------------------------------------
     */

    // 비밀번호 암호화
    private String encodePassword(String passBefore) {
        log.info("새로운 유저 비밀번호 : " + passBefore);
        return passwordEncoder.encode(passBefore);
    }

    // 인증번호 랜덤
    private String generatePassword() {
        return RandomStringUtils.randomNumeric(6);
    }

    private void saveProfileImage(User user, MultipartFile profileImage) throws IOException, NotAnImageFileException {
        if (profileImage != null) { // user/home/warine/user/rick
            if(!Arrays.asList(IMAGE_JPEG_VALUE, IMAGE_PNG_VALUE, IMAGE_GIF_VALUE).contains(profileImage.getContentType())) {
                throw new NotAnImageFileException(profileImage.getOriginalFilename() + NOT_AN_IMAGE_FILE);
            }
            Path userFolder = Paths.get(USER_FOLDER + user.getUsername()).toAbsolutePath().normalize();
            if(!Files.exists(userFolder)) {
                Files.createDirectories(userFolder);
                log.info(DIRECTORY_CREATED + userFolder);
            }
            Files.deleteIfExists(Paths.get(userFolder + user.getUsername() + DOT + JPG_EXTENSION));
            Files.copy(profileImage.getInputStream(), userFolder.resolve(user.getUsername() + DOT + JPG_EXTENSION), REPLACE_EXISTING);
            user.userProfileImageUpdate(setProfileImageUrl(user.getUsername()));
            userRepository.save(user);
            log.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        }
    }

    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXTENSION).toUriString();
    }


    private String getTemporaryProfileImageUrl(String username) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(DEFAULT_USER_IMAGE_PATH + username).toUriString();
    }


}
