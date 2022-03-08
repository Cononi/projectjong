package com.winesee.projectjong.domain.board;

import com.winesee.projectjong.domain.basedefault.BaseTime;
import com.winesee.projectjong.domain.user.User;
import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Comment extends BaseTime {

    // 코멘트 id
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long comments_id;

    // 연관 게시글
    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    // 작성자
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // 작성내용
    private String comment;

}
