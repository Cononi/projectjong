package com.winesee.projectjong.domain.board.dto;

import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.user.User;
import com.winesee.projectjong.domain.user.dto.UserRequest;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import com.winesee.projectjong.domain.wine.Wine;
import com.winesee.projectjong.domain.wine.dto.WineResponse;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


import javax.persistence.*;
import javax.validation.constraints.*;
import java.io.Serializable;


@Getter
@NoArgsConstructor
public class PostRequest implements Serializable {

    //id

    private Long idPostData;
    // 제목
    @NotBlank(message = "제목 입력은 필수 입니다.")
    @Size(min = 1, max = 100, message = "제목은 최소 1자리부터 최대 100자리 입니다.")
    private String titPostData;

    // 와인 정보 ( 자동 완성 기능 )
    @NotNull
    private Long winePostData;

    // 와인 빈티지
    @NotBlank(message = "빈티지 입력은 필수 입니다.")
    @Pattern(regexp = "^[0-9]{1,4}$", message = "숫자만 입력 가능합니다.")
    private String vinPostData;

    // 바디 1-5
    @Positive(message = "숫자만 입력 가능합니다.")
    @Min(value = 1, message = "최소 1점 부터 입니다.")
    @Max(value = 5, message = "최대 5점 이하 입니다.")
    private int bodyPostData;

    // 당도 1-5
    @Positive(message = "숫자만 입력 가능합니다.")
    @Min(value = 1, message = "최소 1점 부터 입니다.")
    @Max(value = 5, message = "최대 5점 이하 입니다.")
    private int sugPostData;

    // 산도 1-5
    @Positive(message = "숫자만 입력 가능합니다.")
    @Min(value = 1, message = "최소 1점 부터 입니다.")
    @Max(value = 5, message = "최대 5점 이하 입니다.")
    private int aciPostData;

    // 가격
    @Positive(message = "숫자만 입력 가능합니다.")
    private int priPostData;

    // 알콜
    @Positive(message = "숫자만 입력 가능합니다.")
    @Min(value = 0, message = "최소 0도 부터 입니다.")
    @Max(value = 100, message = "최대 100도 이하 입니다.")
    private int alcPostData;

    // 최종 점수 ( 1 ~ 100 )
    @Positive(message = "숫자만 입력 가능합니다.")
    @Min(value = 1, message = "최소 1점 부터 입니다.")
    @Max(value = 100, message = "최대 100점 이하 입니다.")
    private int scPostData;

    // 간단한 내용
//    @Lob
    @NotBlank(message = "내용 입력은 필수 입니다.")
    private String conPostData;


    @Builder
    public PostRequest(Long postId,String title, Long wineId, String vintage , int bodyCount, int sugarCount, int acidityCount, int price, int alcohol, int score, String contents) {
        this.idPostData = postId;
        this.titPostData = title;
        this.winePostData = wineId;
        this.vinPostData = vintage;
        this.bodyPostData = bodyCount;
        this.sugPostData = sugarCount;
        this.alcPostData = acidityCount;
        this.priPostData = price;
        this.aciPostData = alcohol;
        this.scPostData = score;
        this.conPostData = contents;
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
