package com.winesee.projectjong.domain.board.dto;

import com.winesee.projectjong.domain.board.Comment;
import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.user.User;
import lombok.Builder;
import lombok.Getter;


@Getter
public class CommentRequest {

    // 코멘트 번호
    private Long commentsId;

    // 연관 게시글
    private Post post;

    // 작성자
    private User user;

    // 작성내용
    private String comment;


    @Builder
    public CommentRequest(Long commentsId, Post post, User user, String comment) {
        this.commentsId = commentsId;
        this.post = post;
        this.user = user;
        this.comment = comment;
    }

    public Comment toEntity(){
        return Comment.builder()
                .commentsId(commentsId)
                .post(post)
                .user(user)
                .comment(comment)
                .build();
    }


}
