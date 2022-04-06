package com.winesee.projectjong.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.winesee.projectjong.domain.board.Comment;
import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.user.User;
import lombok.Getter;

import java.time.LocalDateTime;


@Getter
public class CommentResponse {


    // 코멘트 번호
    private Long commentId;

//    // 연관 게시글
//    private PostResponse postId;

    // 작성자
    private String userId;

    // 유저 번호
    private Long num;

    // 작성내용
    private String comment;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createDate;

    public CommentResponse(Comment entity) {
        this.commentId = entity.getCommentId();
        this.userId = entity.getUserId().getName();
        this.comment = entity.getComment();
        this.num = entity.getUserId().getId();
        this.createDate = entity.getCreateDate();
    }
}
