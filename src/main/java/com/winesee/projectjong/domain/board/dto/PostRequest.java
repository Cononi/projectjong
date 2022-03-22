package com.winesee.projectjong.domain.board.dto;

import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.user.User;
import com.winesee.projectjong.domain.user.dto.UserRequest;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import com.winesee.projectjong.domain.wine.Wine;
import com.winesee.projectjong.domain.wine.dto.WineResponse;
import lombok.Builder;
import lombok.Getter;


import javax.persistence.*;
import javax.validation.constraints.*;


@Getter

public class PostRequest {

    // 제목
    @NotBlank(message = "제목 입력은 필수 입니다.")
    @Size(min = 1, max = 100, message = "제목은 최소 1자리부터 최대 100자리 입니다.")
    private String title;

    // 와인 정보 ( 자동 완성 기능 )
    @NotNull
    private Long wineId;

    // 와인 빈티지
    @NotBlank(message = "빈티지 입력은 필수 입니다.")
    @Pattern(regexp = "^[0-9]{1,4}$", message = "숫자만 입력 가능합니다.")
    private String vintage;

    // 바디 1-5
    @Positive(message = "숫자만 입력 가능합니다.")
    @Min(value = 1, message = "최소 1점 부터 입니다.")
    @Max(value = 5, message = "최대 5점 이하 입니다.")
    private int bodyCount;

    // 당도 1-5
    @Positive(message = "숫자만 입력 가능합니다.")
    @Min(value = 1, message = "최소 1점 부터 입니다.")
    @Max(value = 5, message = "최대 5점 이하 입니다.")
    private int sugarCount;

    // 산도 1-5
    @Positive(message = "숫자만 입력 가능합니다.")
    @Min(value = 1, message = "최소 1점 부터 입니다.")
    @Max(value = 5, message = "최대 5점 이하 입니다.")
    private int acidityCount;

    // 가격
    @Positive(message = "숫자만 입력 가능합니다.")
    private int price;

    // 알콜
    @Positive(message = "숫자만 입력 가능합니다.")
    private int alcohol;

    // 최종 점수 ( 1 ~ 100 )
    @Positive(message = "숫자만 입력 가능합니다.")
    @Min(value = 1, message = "최소 1점 부터 입니다.")
    @Max(value = 100, message = "최대 100점 이하 입니다.")
    private int score;

    // 간단한 내용
//    @Lob
    @NotBlank(message = "내용 입력은 필수 입니다.")
    private String contents;


    @Builder
    public PostRequest(String title, Long wineId, String vintage , int bodyCount, int sugarCount, int acidityCount, int price, int alcohol, int score, String contents) {
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
    }

//    public Post toEntity(){
//        return Post.builder()
//                .title(title)
//                .wineId()
//                .build();
//    }
//
//    private void wineIdChange(Long wine) {
//        this.wine_id = wine;
//    }
}
