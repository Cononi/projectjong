package com.winesee.projectjong.domain.board.dto;

import com.winesee.projectjong.domain.board.Comment;
import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.user.User;
import com.winesee.projectjong.domain.wine.Wine;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class PostResponse {

    // 번호
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

    // 덧글
    private List<Comment> comments = new ArrayList<>();


    public PostResponse(Post entity) {
        this.post_id = entity.getPost_id();
        this.user_id = entity.getUser_id();
        this.title = entity.getTitle();
        this.wine_id = entity.getWine_id();
        this.bodyCount = entity.getBodyCount();
        this.sugarCount = entity.getSugarCount();
        this.acidityCount = entity.getAcidityCount();
        this.price = entity.getPrice();
        this.contents = entity.getContents();
        this.comments = entity.getComments();
    }
}
