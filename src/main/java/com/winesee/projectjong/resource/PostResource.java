package com.winesee.projectjong.resource;

import com.winesee.projectjong.config.exception.UserNotFoundException;
import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.board.dto.PostRequest;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import com.winesee.projectjong.service.comment.CommentService;
import com.winesee.projectjong.service.post.PostService;
import com.winesee.projectjong.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.ObjectUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PostResource {

    private final PostService postService;
    private final UserService userService;

    // 저장
    @PostMapping(value = "v1/post", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postCreate(HttpServletRequest request, @RequestBody @Validated PostRequest post, Errors error, @AuthenticationPrincipal UserResponse user){
        if(request.getSession().getAttribute(user.getUsername()).equals(true)){
            if(error.hasErrors()) {
                return new ResponseEntity<>("입력오류",HttpStatus.BAD_REQUEST);
            }
            request.getSession().setAttribute(user.getUsername(),false);
            return new ResponseEntity<>(postService.postCreate(post,user), HttpStatus.OK);
        } else {
            return null;
        }
    }

    // 수정
    @PutMapping(value = "v1/post", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postEdit(HttpServletRequest request, @RequestBody @Validated PostRequest post, Errors error, @AuthenticationPrincipal UserResponse user){
        if(request.getSession().getAttribute(user.getUsername()).equals(true)){
            if(error.hasErrors()) {
                return new ResponseEntity<>("입력오류", HttpStatus.BAD_REQUEST);
            }
            request.getSession().setAttribute(user.getUsername(),false);
            return new ResponseEntity<>(postService.postEdit(post,user), HttpStatus.OK);
            } else {
                return null;
        }
    }


    // 삭제.
    @DeleteMapping(value = "v1/post/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void postDelete(@PathVariable Long number, @AuthenticationPrincipal UserResponse user){
        postService.postDelete(number, user);
    }

    // 와인 상세페이지에서 사람들이 작성한 와인 테스팅
    @GetMapping("v1/post/{pageNum}/list/{wineId}")
    public ResponseEntity<?> list(@PathVariable("pageNum") Integer pageNum, @PathVariable("wineId") Long wineId){
        return new ResponseEntity<>(postService.postListSearch(pageNum-1,wineId),HttpStatus.OK);
    }

    // 내가 작성한 와인 목록
    @GetMapping("v1/account/post/wine/{pageNum}")
    public ResponseEntity<?> accountPostList(@AuthenticationPrincipal UserResponse user, @PathVariable("pageNum") Integer pageNum){
        return new ResponseEntity<>(postService.myPostWineList(user,pageNum-1),HttpStatus.OK);
    }


    // 내가 작성한 와인 상세 포스팅 목록
    @GetMapping("v1/post/{pageNum}/wine/{wineId}")
    public ResponseEntity<?> accountPostMyList(@AuthenticationPrincipal UserResponse user,@PathVariable("pageNum") Integer pageNum, @PathVariable("wineId") Long wineId){
        return new ResponseEntity<>(postService.postMyListSearch(user,pageNum-1,wineId),HttpStatus.OK);
    }



}
