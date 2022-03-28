package com.winesee.projectjong.domain.wine.dto;

import com.winesee.projectjong.domain.wine.Country;
import com.winesee.projectjong.domain.wine.Wine;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class WineMyPostResponse implements Serializable {

    // 와인 번호
    private Long wineId;

    // 와인 라벨 한글네임
    private String displayNameKo;

    // 와인 이미지 주소
    private String wineImageUrl;

    // 생산나라20
    private Country country;

    // 최종 토탈 점수
    private int averageScore;


    public WineMyPostResponse(Wine entity) {
        this.wineId = entity.getWineId();
        this.displayNameKo = entity.getDisplayNameKo();
        this.wineImageUrl = entity.getWineImageUrl();
        this.averageScore = entity.getAverageScore();
        this.country = entity.getCountry();
    }

}
