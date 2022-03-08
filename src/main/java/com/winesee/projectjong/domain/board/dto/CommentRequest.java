package com.winesee.projectjong.domain.board.dto;

import com.winesee.projectjong.domain.board.Comment;
import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.user.User;
import lombok.Builder;
import lombok.Getter;


@Getter
public class CommentRequest {

    // 코멘트 번호
    private Long comments_id;

    // 연관 게시글
    private Post post;

    // 작성자
    private User user;

    // 작성내용
    private String comment;


    @Builder
    public CommentRequest(Long comments_id, Post post, User user, String comment) {
        this.comments_id = comments_id;
        this.post = post;
        this.user = user;
        this.comment = comment;
    }

    public Comment toEntity(){
        return Comment.builder()
                .comments_id(comments_id)
                .post(post)
                .user(user)
                .comment(comment)
                .build();
    }


}
