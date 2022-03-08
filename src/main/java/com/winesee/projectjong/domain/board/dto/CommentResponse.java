package com.winesee.projectjong.domain.board.dto;

import com.winesee.projectjong.domain.board.Comment;
import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.user.User;
import lombok.Getter;


@Getter
public class CommentResponse {


    // 코멘트 번호
    private Long comments_id;

    // 연관 게시글
    private Post post;

    // 작성자
    private User user;

    // 작성내용
    private String comment;

    public CommentResponse(Comment entity) {
        this.comments_id = entity.getComments_id();
        this.post = entity.getPost();
        this.user = entity.getUser();
        this.comment = entity.getComment();
    }
}
