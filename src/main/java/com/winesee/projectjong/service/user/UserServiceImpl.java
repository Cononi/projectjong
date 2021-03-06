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

    // User CRUD ??????????????? JpaRepsitory ??????
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final EmailCodeRepository emailCodeRepository;
    private final MailService mailService;

    /*
    -    ?????? ??? ?????? ????????? ????????? ????????????
    Authentication(??????) ???????????? ???????????? ???????????????.
    -- Method : Login Form, HTTP authentication, Custom auth. method ??????
    --------------------------------------------------------------------------------------------------------
    ????????? ????????? ?????? ??? ?????? ????????? ???????????????. ???????????? ???????????? ????????? ???????????? ?????? ????????? ?????????????
    ????????? ???????????? ???????????? ????????????. ????????? ????????????????????? ?????????????????? ??????????????? ????????? ?????? ??? ??????????????? ???????????? ?????????.
    ????????? ?????? ????????? ???????????? ???????????? ????????????. ????????? ????????? ?????? ????????? ????????? ?????? ????????????
    ??? ???????????????????????? ??? ??????????????? ?????? ?????? ???????????? ????????????????????? ???????????? ????????? ????????? ??????????????? ?????? ????????? ????????? ???????????? ??????.
    ?????? Jeon??? ???????????? ?????? ????????? ????????? jeon?????? ????????? ????????????, ????????? ????????? ??? ????????? ????????? ???????????? jeon?????? ???????????? ?????????.
    ???????????? ???????????? ????????? ????????? ?????? ???????????? ?????? ????????? ???????????? ???????????????.

    -   ?????? ??? ?????? ????????? ????????? ?????? ????????????
    Authorization(?????? ??????) ???????????? ???????????? ??? ?????? ????????? ????????? ??????
    -------------------------------------------------------------------------------------------------------
    ?????? ???????????? ?????? ???????????? ?????????????????? ???????????? ??? ????????? ???????????? ?????? ????????? ????????? ????????? ?????? ???????????? ????????????.
    ????????? ?????? ????????? ???????????? ????????? ??????????????? ????????? ???????????? ????????????. ?????? ????????? ????????? ???????????? ?????? ?????? ??????????????? ???????????? ??? ????????????.
    ????????? ?????? ????????? ?????????????????? ?????? ????????? ???????????? ?????? ????????????????????? ??????????????? ??? ????????? ?????? ????????? ?????? ???????????????????????? ????????? ??? ????????????.
    ????????? ????????? ?????? ????????? ???????????? ????????????. ?????? ????????? ???????????? ????????? ??? ??? ????????????.
    ???????????? ?????? ?????? ????????????????????? ??????????????? ???????????? ??????????????? ????????????????????? ????????? ????????? ?????? ??? ??? ????????????.
    ????????? ???????????? ?????? ????????? ??? ??? ????????????.
    Method : Access Control URLs, Access Control List (ACLs)

    ????????? ?????? ????????? ?????? ????????? ?????? ???????????? ???????????? ??? ????????? ????????? ???????????????. ????????? ???????????? ?????? ?????? ?????????.
    ?????? ????????? ???????????? ?????????? ???????????????.
    ????????? ????????? ????????? ?????? ????????? ?????? ?????? ????????? ??? ??? ?????????????
    ????????? ?????? ?????? ????????? ??? ??? ????????????? ???????????????.
     */
    /**
     * ????????? ????????? ??????????????? ?????? ????????? ???????????? ???????????? ???????????? ????????? ??? ??????.
     * @param username ????????? ??????
     * @return UserDetails ?????????
     * @throws UsernameNotFoundException ????????? ???????????? ????????? Excepiton ????????????
     */
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = Optional.ofNullable(userRepository.findByUsername(username)).orElseThrow(() -> new UsernameNotFoundException("?????? ?????????"));
        validateLoginAttempt(user);
//            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
//            authorities.add(new SimpleGrantedAuthority(user.getRoleKey()));
        return new UserResponse(user);
    }

    /*-----------------------------------------------
        Regiser - ?????? ??????
     -----------------------------------------------*/
    @Override
    public void register(UserRequest userRequest) throws IOException, UserNotFoundException, EmailExistException, UsernameExistException, MessagingException {
        // ?????? ??????
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
        // Redis ?????? ??????
        EmailCode emailCode = new EmailCode(userRequest.getEmail());
        emailCodeRepository.save(emailCode);
        // ????????? ??????
        mailService.sendRegisterMail(userRequest.getUsername(),userRequest.getEmail(),emailCode.getCode(), emailCode.getId());
    }

    /*-----------------------------------------------
        emailConfirm - ????????? ??????
     -----------------------------------------------*/

    @Override
    public List<String> emailConfirm(String email, String authId, String authKey) throws MessagingException {
        List<String> msg = new ArrayList<>();
        // ???????????? ???????????? ????????? ????????? ????????????.
        User user = Optional.ofNullable(userRepository.findByEmailAndIsEmailEnabledFalse(email)).orElseThrow(() -> new MessagingException("???????????? ?????? ?????? ?????????."));
        // ?????? ????????? ?????? ???????????? authId??? ?????? RedisServer??? ???????????? authId??? ????????? ?????? ???????????? ?????? ????????? ?????? ?????? ??????
        EmailCode emailCode = emailCodeRepository.findById(authId)
                .orElseGet(() ->emailCodeRepository.save(new EmailCode(email))
        );
        // ???????????? ???????????? ???????????? ???????????? ???????????? ????????? ???????????? authId??? ???????????? ????????? ????????? ?????????????????? ????????? ?????????
       if(emailCode.getId().equals(authId)) {
           user.userSecurityLockUpdate(user.getIsActive(), user.getIsNonLocked(), true);
           userRepository.save(user);
           emailCodeRepository.delete(emailCode);
           msg.add("??????????????? ??????????????????!");
           msg.add("????????? ?????????????????????. <br/>????????? ????????? ?????????????????? ???????????????.");
       } else {
           mailService.sendRegisterMail(user.getUsername(),user.getEmail(),emailCode.getCode(), emailCode.getId());
           msg.add("???????????? ?????? ?????? ?????????.");
           msg.add("????????? ????????? ?????????????????????. <br/>???????????? ?????? ??????????????? ???????????? ?????? ?????? ?????????. <br/>????????? ???????????? ???????????????.");
       }
       // ????????? ??????
       return msg;
    }


    /*-----------------------------------------------
        getUser - ?????? ?????? ?????? ??????
         -----------------------------------------------*/
    @Override
    public UserResponse getUser(String username) {
        return new UserResponse(userRepository.findByUsername(username));
    }

    /*-----------------------------------------------
    getUserId - ?????? ?????? Id????????? ?????? ??????
     -----------------------------------------------*/

    @Override
    public UserInfoResponse getUserId(Long id) throws UserNotFoundException {
        return new UserInfoResponse(userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("???????????? ?????? ?????? ?????????.")));
    }

    @Override
    public UserResponse getUserIdResponse(Long username) throws UserNotFoundException {
        return new UserResponse(userRepository.findById(username).orElseThrow(() -> new UserNotFoundException("???????????? ?????? ?????? ?????????.")));
    }

    /*-----------------------------------------------
            getUsers - ?????? ?????? ??????
             -----------------------------------------------*/
    @Override
    public Page<UserInfoResponse> getUsers(int page) {
        Pageable pageable = PageRequest.of(page, 24, Sort.by("id"));
        return  userRepository.findAll(pageable).map(UserInfoResponse::new);
    }

    /*-----------------------------------------------
    updateProfile - ?????? ?????????  ????????????.
    -----------------------------------------------*/
    @Override
    public UserResponse updateProfile(UserResponse userinfo, String name, MultipartFile profileImage) throws IOException, ProfileErrorException, NotAnImageFileException {
        // ???????????? ????????? ???????????? ????????????
        if(!userinfo.getName().equals(name)){
            User userSearch = findByName(name);
            // ????????? ?????????????????? ??????
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
    updateProfilePassword - ?????? ????????????  ????????????.
    -----------------------------------------------*/
    @Override
    public UserResponse updateProfilePassword(UserResponse userinfo, PasswordChangeRequest request) throws ProfileErrorException {
        if(request.getPassword().isEmpty() || request.getConfirmPassword().isEmpty()){
            throw new ProfileErrorException(NULL_USER_PASSWORD_SUCCESSFULLY);
        }
        // ???????????? ??????????????? ????????????????????? ???????????? ???????????? ????????? ????????????.
        if(request.getPassword().equals(request.getConfirmPassword())){
            // ?????? ???????????? ????????? ????????? ?????????
            User user = findByUsername(userinfo.getUsername());
            // ???????????? ??????
            user.userPasswordUpdate(passwordEncoder.encode(request.getPassword()));
            // ????????? ?????? ?????? ??? ??????
            return new UserResponse(userRepository.save(user));
        } else {
            throw new ProfileErrorException(NO_USER_PASSWORD_SUCCESSFULLY);
        }
    }

    /*-----------------------------------------------
    updatePassword - ?????? ???????????? ??????
    -----------------------------------------------*/
    @Override
    public boolean passwordAuth(String password, String passwordAuth) {
        return passwordEncoder.matches(passwordAuth, password);
    }

    /*-----------------------------------------------
        findUserbyUsername - ?????? ???????????? ?????? ??????
        -----------------------------------------------*/
    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /*-----------------------------------------------
      findUserByEmail - ?????? ???????????? ?????? ??????
      -----------------------------------------------*/
    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /*-----------------------------------------------
  findUserByEmail - ?????? ???????????? ?????? ??????
  -----------------------------------------------*/
    @Override
    public User findByName(String name) {
        return userRepository.findByName(name);
    }


    /*-----------------------------------------------
      deleteUser - ?????? ??????
    -----------------------------------------------*/
    @Override
    public void deleteUser(long id) {
        userRepository.deleteById(id);
    }


    /*-----------------------------------------------
          userCheck - Api??? - ???????????? ????????? ??????
    -----------------------------------------------*/
    @Override
    public String userCheck(String username, String uri) throws EmailExistException, UsernameExistException {
        String msg = "";
        if(uri.equals("/api/find/username/"+username)){
            validateNewUsernameAndEmail(username,null,null);
            msg= "("+ username +")??? ??????????????? ????????? ?????????.";
        } else if(uri.equals("/api/find/email/"+username)) {
            validateNewUsernameAndEmail(null,username,null);
            msg= "("+ username +")??? ??????????????? ????????? ?????????.";
        } else {
            validateNewUsernameAndEmail(null,null,username);
            msg= "("+ username +")??? ??????????????? ????????? ?????????.";
        }
        return msg;
    }

    /*-----------------------------------------------
      userValidateCheck - ??????????????? Error ??????.
    -----------------------------------------------*/
    @Override
    public Errors userValidateCheck(Errors errors, UserRequest user) {
        List<User> findAll = userRepository.findAllByUsernameOrEmailOrName(user.getUsername(), user.getEmail(), user.getName());
        for(User find : findAll){
            if(user.getUsername().equalsIgnoreCase(find.getUsername())){
                errors.rejectValue("username","username.error","?????? ???????????? ?????? ????????? ?????????.");
            } else if(user.getEmail().equalsIgnoreCase(find.getEmail())){
                errors.rejectValue("email","email.error", "?????? ???????????? ?????? ????????? ?????????.");
            } else if(user.getName().equalsIgnoreCase(find.getName())){
                errors.rejectValue("name","name.error", "?????? ???????????? ?????? ????????? ?????????.");
            }
        }
        return errors;
    }


    /*-----------------------------------------------
      userPasswordFind - ???????????? ??????
    -----------------------------------------------*/
    @Override
    public String userPasswordFind(UserPasswordFindRequest request) {
        String errorMsg = "";
        User user = userRepository.findByEmail(request.getEmail());
        if(ObjectUtils.isEmpty(user)) {
            errorMsg = "??????????????? ???????????? ????????????. ?????? ????????????.";
        } else if(request.getUsername().equals(user.getUsername()) && request.getEmail().equals(user.getEmail())){
            String pass = generatePassword();
            user.userPasswordUpdate(encodePassword(pass));
            userRepository.save(user);
            mailService.sendFindPassMail(user.getName(),user.getEmail(),pass);
            errorMsg = "?????? ??????????????? ???????????? ???????????? ?????????????????????. ????????? ??? ?????????????????? ????????????.";
        } else {
            errorMsg = "??????????????? ???????????? ????????????. ?????? ????????????.";
        }
        return errorMsg;
    }

    /*-----------------------------------------------
      ???????????? ?????? - Admin Page
    -----------------------------------------------*/
    @Override
    public void adminPageUserEdit(UserAdminEditRequest request, Long userId) {
        User user = userRepository.getById(userId);
        // ?????? ??????
        user.roleEdit(request.getRoles());
        // ?????? ?????? ??????
        user.userProfileAdminSet(request.getName(),request.getEmail(),user.getProfileImageUrl());
        // ?????? ????????????
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

    // ???????????? ?????????
    private String encodePassword(String passBefore) {
        return passwordEncoder.encode(passBefore);
    }

    // ???????????? ??????
    private String generatePassword() {
        return RandomStringUtils.randomNumeric(6);
    }

    // ????????? ?????????
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
