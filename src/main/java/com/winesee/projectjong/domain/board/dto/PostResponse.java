package com.winesee.projectjong.domain.board.dto;

import com.winesee.projectjong.domain.basedefault.BaseTime;
import com.winesee.projectjong.domain.board.Comment;
import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.user.User;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import com.winesee.projectjong.domain.wine.Wine;
import com.winesee.projectjong.domain.wine.dto.WineResponse;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PostResponse {

    // 번호
    private Long postId;

    // 작성자
    private String userId;

    // 유저번호
    private Long userNum;

    // 제목
    private String title;

    // 와인 정보 ( 자동 완성 기능 )

    private WineResponse wineId;

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
//    @Lob
    private String contents;

    // 덧글
    private List<Comment> comments = new ArrayList<>();

    private LocalDateTime modifieDate;


    public PostResponse(Post entity) {
        this.postId = entity.getPostId();
        this.userId = entity.getUserId().getName();
        this.userNum = entity.getUserId().getId();
        this.title = entity.getTitle();
        this.wineId = new WineResponse(entity.getWineId());
        this.vintage = entity.getVintage();
        this.bodyCount = entity.getBodyCount();
        this.sugarCount = entity.getSugarCount();
        this.acidityCount = entity.getAcidityCount();
        this.price = entity.getPrice();
        this.alcohol = entity.getAlcohol();
        this.score = entity.getScore();
        this.contents = entity.getContents();
        this.comments = entity.getComments();
        this.modifieDate = entity.getModifieDate();
    }

}
