package com.winesee.projectjong.domain.board;

import com.winesee.projectjong.domain.basedefault.BaseTime;
import com.winesee.projectjong.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Comment extends BaseTime {

    // 코멘트 id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentsId;

    // 연관 게시글
    @ManyToOne
    @JoinColumn(name = "postId")
    private Post post;

    // 작성자
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    // 작성내용
    private String comment;


    @Builder
    public Comment(Long commentsId, Post post, User user, String comment) {
        this.commentsId = commentsId;
        this.post = post;
        this.user = user;
        this.comment = comment;
    }
}