package com.winesee.projectjong.domain.board;

import com.winesee.projectjong.domain.basedefault.BaseTime;
import com.winesee.projectjong.domain.user.User;
import com.winesee.projectjong.domain.wine.Wine;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Table(name="post")
@NoArgsConstructor
public class Post extends BaseTime {

    // 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    // 작성자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User userId;

    // 제목
    @Column(length = 100)
    private String title;

    // 와인 정보 ( 자동 완성 기능 )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="wineId")
    private Wine wineId;

    // 와인 빈티지
    private String vintage;

    // 바디 1-5
    private int bodyCount;

    // 당도 1-5
    private int sugarCount;

    // 산도 1-5
    private int acidityCount;

    // 가격
    private int price;

    // 알콜
    private int alcohol;

    // 최종 점수 ( 1 ~ 100 )
    private int score;

    // 간단한 내용
    @Lob
    private String contents;

    // 덧글
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();


    @Builder
    public Post(Long postId, User userId, String title, Wine wineId, String vintage, int bodyCount, int sugarCount, int acidityCount, int price, int alcohol, int score, String contents, List<Comment> comments) {
        this.postId = postId;
        this.userId = userId;
        this.title = title;
        this.wineId = wineId;
        this.vintage = vintage;
        this.bodyCount = bodyCount;
        this.sugarCount = sugarCount;
        this.acidityCount = acidityCount;
        this.price = price;
        this.alcohol = alcohol;
        this.score = score;
        this.contents = contents;
        this.comments = comments;
    }

    public void Update(String title, String vintage, int bodyCount, int sugarCount, int acidityCount, int price, int alcohol, int score, String contents) {
        this.title = title;
        this.vintage = vintage;
        this.bodyCount = bodyCount;
        this.sugarCount = sugarCount;
        this.acidityCount = acidityCount;
        this.price = price;
        this.alcohol = alcohol;
        this.score = score;
        this.contents = contents;
    }
}
