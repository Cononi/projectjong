package com.winesee.projectjong.resource;

import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.board.dto.PostRequest;
import com.winesee.projectjong.domain.board.dto.PostResponse;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import com.winesee.projectjong.domain.wine.Country;
import com.winesee.projectjong.service.post.PostService;
import com.winesee.projectjong.service.wine.WineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class PostResource {

    private final PostService postService;

    @PostMapping(value = "v1/post", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postCreate(@RequestBody @Validated PostRequest post, Errors error, @AuthenticationPrincipal UserResponse user){

        if(error.hasErrors()) {
            return new ResponseEntity<>(error.getFieldErrors().stream().collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage)
            ), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(postService.postCreate(post,user), HttpStatus.OK);
    }

    // 만들어야함 수
    @PutMapping(value = "v1/post", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postEdit(@RequestBody @Validated PostRequest post, Errors error, @AuthenticationPrincipal UserResponse user){
        if(error.hasErrors()) {
            return new ResponseEntity<>(error.getFieldErrors().stream().collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage)
            ), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>(postService.postCreate(post,user), HttpStatus.OK);
    }

    // 만들어야함
    // 모달창 생성후 처리
    @DeleteMapping(value = "v1/post/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
    public void postDelete(@PathVariable Long number, @AuthenticationPrincipal UserResponse user){
        postService.postDelete(number, user);
    }

    @GetMapping("v1/post/{pageNum}/list/{wineId}")
    public ResponseEntity<?> list(@PathVariable("pageNum") Integer pageNum, @PathVariable("wineId") Long wineId){
        return new ResponseEntity<>(postService.postListSearch(pageNum-1,wineId),HttpStatus.OK);
    }
}
