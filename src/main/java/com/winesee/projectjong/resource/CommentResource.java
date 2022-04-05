package com.winesee.projectjong.resource;

import com.winesee.projectjong.domain.board.dto.CommentRequest;
import com.winesee.projectjong.domain.board.dto.PostRequest;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import com.winesee.projectjong.service.comment.CommentService;
import com.winesee.projectjong.service.post.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.Errors;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class CommentResource {

    private final CommentService commentService;

    // 저장
    @PostMapping(value = "v1/comment", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> postCreate(HttpServletRequest request, @RequestBody @Validated CommentRequest comment, Errors error, @AuthenticationPrincipal UserResponse user){
            if(error.hasErrors()) {
                return new ResponseEntity<>("입력오류",HttpStatus.BAD_REQUEST);
            }
            request.getSession().setAttribute(user.getUsername(),false);
            return new ResponseEntity<>(commentService.createComment(comment,user), HttpStatus.OK);
    }
//
//    // 수정
//    @PutMapping(value = "v1/post", produces = MediaType.APPLICATION_JSON_VALUE)
//    public ResponseEntity<?> postEdit(HttpServletRequest request, @RequestBody @Validated PostRequest post, Errors error, @AuthenticationPrincipal UserResponse user){
//        if(request.getSession().getAttribute(user.getUsername()).equals(true)){
//            if(error.hasErrors()) {
//                return new ResponseEntity<>("입력오류", HttpStatus.BAD_REQUEST);
//            }
//            request.getSession().setAttribute(user.getUsername(),false);
//            return new ResponseEntity<>(postService.postEdit(post,user), HttpStatus.OK);
//            } else {
//                return null;
//        }
//    }


//    // 삭제.
//    @DeleteMapping(value = "v1/post/{number}", produces = MediaType.APPLICATION_JSON_VALUE)
//    public void postDelete(@PathVariable Long number, @AuthenticationPrincipal UserResponse user){
//        postService.postDelete(number, user);
//    }


    // 코멘트 리스트
    @GetMapping("v1/comment/{item}/{page}")
    public ResponseEntity<?> tastingPageCommentList(@AuthenticationPrincipal UserResponse user,@PathVariable("item") Long item, @PathVariable("page") Integer page){
        return new ResponseEntity<>(commentService.listComment(item,page-1),HttpStatus.OK);
    }



}
