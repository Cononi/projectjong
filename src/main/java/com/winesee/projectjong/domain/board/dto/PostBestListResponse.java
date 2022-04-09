package com.winesee.projectjong.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.wine.dto.WineResponse;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class PostBestListResponse {

    // 번호
    private Long postId;

    // 유저 사진
    private String userProfileImgUrl;

    // 작성자
    private String userId;

    // 타이틀
    private String title;

    // 와인 정보
//    private WineResponse wineId;

    // 와인 이미지
    private String wineImageUrl;

    // 종합점수
    private int score;

    public PostBestListResponse(Post entity) {
        this.postId = entity.getPostId();
        this.userId = entity.getUserId().getName();
//        this.wineId = new WineResponse(entity.getWineId());
        this.title = entity.getTitle();
        this.wineImageUrl = entity.getWineId().getWineImageUrl();
        this.userProfileImgUrl = entity.getUserId().getProfileImageUrl();
        this.score = entity.getScore();
    }
}
