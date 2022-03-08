package com.winesee.projectjong.resource;

import com.winesee.projectjong.config.HttpResponse;
import com.winesee.projectjong.config.exception.EmailExistException;
import com.winesee.projectjong.config.exception.UserNotFoundException;
import com.winesee.projectjong.config.exception.UsernameExistException;
import com.winesee.projectjong.domain.user.Role;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import com.winesee.projectjong.service.user.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import static com.winesee.projectjong.config.constant.FileConstant.*;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.IMAGE_JPEG_VALUE;


/**
 * 유저 RestController (RestAPI)
 * @author Jhong
 * @version 1.0
 * @since 2022-02-14
 */
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserResource {
    private final UserService userService;

    @GetMapping("users")
    public ResponseEntity<List<UserResponse>> getUsers() {
        return ResponseEntity.ok().body(userService.getUsers());
    }

    @GetMapping("delete/{user}")
    public ResponseEntity<?> ss(@PathVariable("user") long num) throws  IOException {
        userService.deleteUser(num);
        return new ResponseEntity<>("완료", OK);
    }

//    @PostMapping("/user/register")
//    public ResponseEntity<UserResponse> register(@RequestBody UserRequest userRequest) throws  IOException {
//        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/user/register").toUriString());
//        UserResponse user = userService.regiter(userRequest);
//        return new ResponseEntity<>(user, OK);
//    }

    @GetMapping(path = "image/{username}/{fileName}", produces = IMAGE_JPEG_VALUE)
    public byte[] getProfileImage(@PathVariable("username") String username, @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(USER_FOLDER + username + FORWARD_SLASH + fileName));
    }

    @GetMapping(path = "image/profile/{username}", produces = IMAGE_JPEG_VALUE)
    public byte[] getTempProfileImage(@PathVariable("username") String username) throws IOException {
        URL url = new URL(TEMP_PROFILE_IMAGE_BASE_URL + username);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try(InputStream inputStream = url.openStream()) {
            int bytesRead;
            byte[] chunk = new byte[1024];
            while((bytesRead = inputStream.read(chunk)) > 0) {
                byteArrayOutputStream.write(chunk, 0, bytesRead); // 0 - 1024 bytes ( 0 - 1024 bytes )
            }
        }
        return byteArrayOutputStream.toByteArray();
    }

    /*-----------------------------------------------
    getUser - 사용중인 계정 확인
    -----------------------------------------------*/
    @GetMapping(path = {"find/email/{username}", "find/username/{username}", "find/name/{username}"}, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getUser(@AuthenticationPrincipal UserResponse userinfo, @PathVariable("username") String username, HttpServletRequest request) throws UserNotFoundException, EmailExistException, UsernameExistException {
        if(userinfo!=null&& userinfo.getName().equals(username)){
            return response(OK,"변경사항 없음");
        }
        return response(OK,userService.userCheck(username,request.getRequestURI()));
    }


    @Getter
    class EditRoleToUser {
        private String username;
        private Role roleName;
    }

    private ResponseEntity<HttpResponse> response(HttpStatus httpStatus, String message) {
        return new ResponseEntity<>(new HttpResponse(httpStatus.value(), httpStatus,
                httpStatus.getReasonPhrase().toUpperCase(), message.toUpperCase()), httpStatus);
    }

}
