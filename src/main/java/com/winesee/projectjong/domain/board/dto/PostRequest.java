package com.winesee.projectjong.domain.board.dto;

import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.user.User;
import com.winesee.projectjong.domain.wine.Wine;
import lombok.Builder;
import lombok.Getter;


import javax.persistence.*;


@Getter

public class PostRequest {

    private Long post_id;
    // 작성자
    private User user_id;

    // 제목
    private String title;

    // 와인 정보 ( 자동 완성 기능 )
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
//    @Lob
    private String contents;


    @Builder
    public PostRequest(Long post_id, User user_id, String title, Wine wine_id, int bodyCount, int sugarCount, int acidityCount, int price, String contents) {
        this.post_id = post_id;
        this.user_id = user_id;
        this.title = title;
        this.wine_id = wine_id;
        this.bodyCount = bodyCount;
        this.sugarCount = sugarCount;
        this.acidityCount = acidityCount;
        this.price = price;
        this.contents = contents;
    }

    public Post toEntity() {
        return Post.builder()
                .postId(post_id)
                .userId(user_id)
                .title(title)
                .wineId(wine_id)
                .bodyCount(bodyCount)
                .sugarCount(sugarCount)
                .acidityCount(acidityCount)
                .price(price)
                .contents(contents)
                .build();
    }
}
