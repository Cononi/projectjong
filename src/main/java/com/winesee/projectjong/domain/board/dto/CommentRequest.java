package com.winesee.projectjong.domain.board.dto;

import com.winesee.projectjong.domain.board.Comment;
import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@NoArgsConstructor
public class CommentRequest {


    // 연관 게시글
    private Long postId;

    // 작성자
    private User userId;

    // 작성내용
    private String comment;


    @Builder
    public CommentRequest(Long postId, String comment) {
        this.postId= postId;
        this.comment = comment;
    }


}
