package com.winesee.projectjong.service.user;

import com.winesee.projectjong.config.exception.*;
import com.winesee.projectjong.domain.redis.EmailCode;
import com.winesee.projectjong.domain.redis.EmailCodeRepository;
import com.winesee.projectjong.domain.user.Role;
import com.winesee.projectjong.domain.user.User;
import com.winesee.projectjong.domain.user.UserRepository;
import com.winesee.projectjong.domain.user.dto.*;
import com.winesee.projectjong.service.attempt.LoginAttemptService;
import com.winesee.projectjong.service.email.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Errors;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.mail.MessagingException;
import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

import static com.winesee.projectjong.config.constant.FileConstant.*;
import static com.winesee.projectjong.config.constant.UserImplConstant.*;
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
    private final LoginAttemptService loginAttemptService;
    private final EmailCodeRepository emailCodeRepository;
    private final MailService mailService;

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
        User user = Optional.ofNullable(userRepository.findByUsername(username)).orElseThrow(() -> new UsernameNotFoundException("없는 아이디"));
        validateLoginAttempt(user);
//            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
//            authorities.add(new SimpleGrantedAuthority(user.getRoleKey()));
        return new UserResponse(user);
    }

    /*-----------------------------------------------
        Regiser - 회원 가입
     -----------------------------------------------*/
    @Override
    public void register(UserRequest userRequest) throws IOException, UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        // 계정 생성
        userRepository.save(User.builder()
                .name(userRequest.getName())
                .username(userRequest.getUsername())
                .password(encodePassword(userRequest.getPassword()))
                .email(userRequest.getEmail())
                .isActive(true)
                .isNonLocked(true)
                .isEmailEnabled(false)
                .lastLoginDate(null)
                .roles(Role.USER)
                .build());
        // Redis 코드 저장
        EmailCode emailCode = new EmailCode(userRequest.getEmail());
        emailCodeRepository.save(emailCode);
        // 이메일 발송
        mailService.sendRegisterMail(userRequest.getUsername(),userRequest.getEmail(),emailCode.getCode(), emailCode.getId());
    }

    /*-----------------------------------------------
        emailConfirm - 이메일 인증
     -----------------------------------------------*/

    @Override
    public List<String> emailConfirm(String email, String authId, String authKey) throws MessagingException {
        List<String> msg = new ArrayList<>();
        // 이메일이 존재하고 이메일 인증이 안된경우.
        User user = Optional.ofNullable(userRepository.findByEmailAndIsEmailEnabledFalse(email)).orElseThrow(() -> new MessagingException("유효하지 않는 상태 입니다."));
        // 가입 유저가 맞고 해당하는 authId와 실제 RedisServer에 존재하는 authId가 맞으면 코드 데이터를 넣고 아니면 새로 코드 발급
        EmailCode emailCode = emailCodeRepository.findById(authId)
                .orElseGet(() ->emailCodeRepository.save(new EmailCode(email))
        );
        // 이메일이 존재하고 인증되지 않았으며 인증코드 조회가 되는경우 authId를 비교하며 비교후 맞으면 가입완료처리 아니면 재발송
       if(emailCode.getId().equals(authId)) {
           user.userSecurityLockUpdate(user.getIsActive(), user.getIsNonLocked(), true);
           userRepository.save(user);
           emailCodeRepository.delete(emailCode);
           msg.add("회원가입을 축하드립니다!");
           msg.add("인증이 완료되었습니다. <br/>확인을 누르면 메인페이지로 이동합니다.");
       } else {
           mailService.sendRegisterMail(user.getUsername(),user.getEmail(),emailCode.getCode(), emailCode.getId());
           msg.add("유효하지 않은 접근 입니다.");
           msg.add("이메일 인증에 실패하였습니다. <br/>유효하지 않은 코드이거나 유효하지 않은 접근 입니다. <br/>새로운 이메일을 발송합니다.");
       }
       // 메세지 리턴
       return msg;
    }


    /*-----------------------------------------------
        getUser - 해당 유저 정보 조회
         -----------------------------------------------*/
    @Override
    public UserResponse getUser(String username) {
        return new UserResponse(userRepository.findByUsername(username));
    }

    /*-----------------------------------------------
    getUserId - 해당 유저 Id번호로 정보 조회
     -----------------------------------------------*/

    @Override
    public UserInfoResponse getUserId(Long id) throws UserNotFoundException {
        return new UserInfoResponse(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저 입니다.")));
    }

    @Override
    public UserResponse getUserIdResponse(Long username) throws UserNotFoundException {
        return new UserResponse(userRepository.findById(username).orElseThrow(() -> new UserNotFoundException("존재하지 않는 유저 입니다.")));
    }

    /*-----------------------------------------------
            getUsers - 모든 유저 목록
             -----------------------------------------------*/
    @Override
    public Page<UserInfoResponse> getUsers(int page) {
        Pageable pageable = PageRequest.of(page, 24, Sort.by("id"));
        return  userRepository.findAll(pageable).map(UserInfoResponse::new);
    }

    /*-----------------------------------------------
    updateProfile - 유저 프로필  업데이트.
    -----------------------------------------------*/
    @Override
    public UserResponse updateProfile(UserResponse userinfo, String name, MultipartFile profileImage) throws IOException, ProfileErrorException, NotAnImageFileException {
        // 닉네임이 변경할 닉네임과 다를경우
        if(!userinfo.getName().equals(name)){
            User userSearch = findByName(name);
            // 유저가 비여있을경우 실행
            if(ObjectUtils.isEmpty(userSearch)){
                User user = findByUsername(userinfo.getUsername());
                return saveProfileImage(user, name, profileImage);
            } else {
                throw new ProfileErrorException(NAME_ALREADY_EXISTS);
            }
        } else if(ObjectUtils.isNotEmpty(profileImage.getOriginalFilename())) {
            User userSearch = findByName(name);
            return saveProfileImage(userSearch, name, profileImage);
        }
        return saveProfileImage(userinfo, name, profileImage);
    }

    /*-----------------------------------------------
    updateProfilePassword - 유저 비밀번호  업데이트.
    -----------------------------------------------*/
    @Override
    public UserResponse updateProfilePassword(UserResponse userinfo, PasswordChangeRequest request) throws ProfileErrorException {
        if(request.getPassword().isEmpty() || request.getConfirmPassword().isEmpty()){
            throw new ProfileErrorException(NULL_USER_PASSWORD_SUCCESSFULLY);
        }
        // 입력받은 패스워드가 검증패스워드와 같으면서 비여있지 않을때 변경실행.
        if(request.getPassword().equals(request.getConfirmPassword())){
            // 현재 로그인한 유저의 정보를 가져옴
            User user = findByUsername(userinfo.getUsername());
            // 패스워드 변경
            user.userPasswordUpdate(passwordEncoder.encode(request.getPassword()));
            // 변경된 정보 저장 및 반환
            return new UserResponse(userRepository.save(user));
        } else {
            throw new ProfileErrorException(NO_USER_PASSWORD_SUCCESSFULLY);
        }
    }

    /*-----------------------------------------------
    updatePassword - 유저 패스워드 검증
    -----------------------------------------------*/
    @Override
    public boolean passwordAuth(String password, String passwordAuth) {
        return passwordEncoder.matches(passwordAuth, password);
    }

    /*-----------------------------------------------
        findUserbyUsername - 유저 이름으로 정보 조회
        -----------------------------------------------*/
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /*-----------------------------------------------
      findUserByEmail - 유저 이메일로 정보 조회
      -----------------------------------------------*/
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /*-----------------------------------------------
  findUserByEmail - 유저 이메일로 정보 조회
  -----------------------------------------------*/
    @Override
    public User findByName(String name) {
        return userRepository.findByName(name);
    }


    /*-----------------------------------------------
      deleteUser - 유저 삭제
    -----------------------------------------------*/
    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }


    /*-----------------------------------------------
          userCheck - Api용 - 사용중인 아이디 조회
    -----------------------------------------------*/
    @Override
    public String userCheck(String username, String uri) throws EmailExistException, UsernameExistException {
        String msg = "";
        if(uri.equals("/api/find/username/"+username)){
            validateNewUsernameAndEmail(username,null,null);
            msg= "("+ username +")은 사용가능한 아이디 입니다.";
        } else if(uri.equals("/api/find/email/"+username)) {
            validateNewUsernameAndEmail(null,username,null);
            msg= "("+ username +")은 사용가능한 이메일 입니다.";
        } else {
            validateNewUsernameAndEmail(null,null,username);
            msg= "("+ username +")은 사용가능한 닉네임 입니다.";
        }
        return msg;
    }

    /*-----------------------------------------------
      userValidateCheck - 사용중일때 Error 추가.
    -----------------------------------------------*/
    @Override
    public Errors userValidateCheck(Errors errors, UserRequest user) {
        List<User> findAll = userRepository.findAllByUsernameOrEmailOrName(user.getUsername(), user.getEmail(), user.getName());
        for(User find : findAll){
            if(user.getUsername().equalsIgnoreCase(find.getUsername())){
                errors.rejectValue("username","username.error","해당 아이디는 이미 사용중 입니다.");
            } else if(user.getEmail().equalsIgnoreCase(find.getEmail())){
                errors.rejectValue("email","email.error", "해당 이메일은 이미 사용중 입니다.");
            } else if(user.getName().equalsIgnoreCase(find.getName())){
                errors.rejectValue("name","name.error", "해당 닉네임은 이미 사용중 입니다.");
            }
        }
        return errors;
    }


    /*-----------------------------------------------
      userPasswordFind - 비밀번호 찾기
    -----------------------------------------------*/
    @Override
    public String userPasswordFind(UserPasswordFindRequest request) {
        String errorMsg = "";
        User user = userRepository.findByEmail(request.getEmail());
        if(ObjectUtils.isEmpty(user)) {
            errorMsg = "회원정보가 존재하지 않습니다. 확인 바랍니다.";
        } else if(request.getUsername().equals(user.getUsername()) && request.getEmail().equals(user.getEmail())){
            String pass = generatePassword();
            user.userPasswordUpdate(encodePassword(pass));
            userRepository.save(user);
            mailService.sendFindPassMail(user.getName(),user.getEmail(),pass);
            errorMsg = "임시 비밀번호가 가입하신 이메일로 발송되었습니다. 확인후 꼭 변경해주시기 바랍니다.";
        } else {
            errorMsg = "회원정보가 존재하지 않습니다. 확인 바랍니다.";
        }
        return errorMsg;
    }

    /*-----------------------------------------------
      회원정보 수정 - Admin Page
    -----------------------------------------------*/
    @Override
    public void adminPageUserEdit(UserAdminEditRequest request, Long userId) {
        User user = userRepository.getById(userId);
        // 권한 변경
        user.roleEdit(request.getRoles());
        // 기본 정보 변경
        user.userProfileAdminSet(request.getName(),request.getEmail(),user.getProfileImageUrl());
        // 상태 업데이트
        user.userSecurityLockUpdate(request.getIsActive(),request.getIsNonLocked(),request.getIsEmailEnabled());
        userRepository.save(user);
    }


    /*
    #----------------------------------------------------------------------------------------
    Sub - Method.
    #----------------------------------------------------------------------------------------
     */

    private User validateNewUsernameAndEmail(String newUsername, String newEmail, String newName) throws UsernameExistException, EmailExistException {
        User find = userRepository.findByUsernameOrEmailOrName(newUsername,newEmail,newName);
            if(ObjectUtils.isNotEmpty(find)){
                if(find.getUsername().equalsIgnoreCase(newUsername)){
                    throw new UsernameExistException(USERNAME_ALREADY_EXISTS);
                }
                if(find.getEmail().equalsIgnoreCase(newEmail)){
                    throw new EmailExistException(EMAIL_ALREADY_EXISTS);
                }
                if(find.getName().equalsIgnoreCase(newName)){
                    throw new UsernameExistException(NAME_ALREADY_EXISTS);
                }
            }
            return null;
    }

    // 비밀번호 암호화
    private String encodePassword(String passBefore) {
        return passwordEncoder.encode(passBefore);
    }

    // 인증번호 랜덤
    private String generatePassword() {
        return RandomStringUtils.randomNumeric(6);
    }

    // 프로필 업로드
    private UserResponse saveProfileImage(User user , String name, MultipartFile profileImage) throws IOException, NotAnImageFileException {
        if (StringUtils.isNotBlank(profileImage.getOriginalFilename())) { // user/home/warine/user/rick
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
            user.userProfileUpdate(name,setProfileImageUrl(user.getUsername()));
            userRepository.save(user);
            log.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
        } else if(StringUtils.isNotBlank(name) && !name.equals(user.getName())) {
            user.userProfileUpdate(name,user.getProfileImageUrl());
            userRepository.save(user);
        }
        return new UserResponse(user);
    }

    private String setProfileImageUrl(String username) {
        return ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path(USER_IMAGE_PATH + username + FORWARD_SLASH + username + DOT + JPG_EXTENSION).toUriString();
    }


//    private String getTemporaryProfileImageUrl(String username) {
//        return ServletUriComponentsBuilder
//                .fromCurrentContextPath()
//                .path("/assets/images/faces/default.jpg").toString();
//    }

    private void validateLoginAttempt(User user) {
        if(user.getIsNonLocked()) {
            user.userSecurityLockUpdate(user.getIsActive(), !loginAttemptService.hasExceededMaxAttempts(user.getUsername()), user.getIsEmailEnabled());
        } else {
            loginAttemptService.evictUserFromLoginAttemptCache(user.getUsername());
        }
    }


}
