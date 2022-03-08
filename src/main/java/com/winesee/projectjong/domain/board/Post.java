package com.winesee.projectjong.domain.board;

import com.winesee.projectjong.domain.basedefault.BaseTime;
import com.winesee.projectjong.domain.user.User;
import com.winesee.projectjong.domain.wine.Wine;
import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name="post")
public class Post extends BaseTime {

    // 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long post_id;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user_id;

    // 제목
    @Column(length = 100)
    private String title;

    // 와인 정보 ( 자동 완성 기능 )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="wine_id")
    private Wine wine_id;

    // 바디 1-5
    private int bodyCount;

    // 당도 1-5
    private int sugarCount;

    // 산도 1-5
    private int acidityCount;

    // 가격
    private int price;

    // 간단한 내용
    @Lob
    private String contents;

    // 덧글
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();


    @Builder
    public Post(Long post_id, User user_id, String title, Wine wine_id, int bodyCount, int sugarCount, int acidityCount, int price, String contents, List<Comment> comments) {
        this.post_id = post_id;
        this.user_id = user_id;
        this.title = title;
        this.wine_id = wine_id;
        this.bodyCount = bodyCount;
        this.sugarCount = sugarCount;
        this.acidityCount = acidityCount;
        this.price = price;
        this.contents = contents;
        this.comments = comments;
    }
}
