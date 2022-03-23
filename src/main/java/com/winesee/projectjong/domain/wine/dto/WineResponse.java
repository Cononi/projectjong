package com.winesee.projectjong.domain.wine.dto;

import com.winesee.projectjong.domain.wine.Country;
import com.winesee.projectjong.domain.wine.Wine;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Getter
@NoArgsConstructor
public class WineResponse implements Serializable {

    // 와인 번호
    private Long wineId;

    // 와인 라벨 풀네임
    private String displayName;

    // 와인 라벨 한글네임
    private String displayNameKo;

    // 와인 품종 정보
    private String grapeList;

    // 생산자
    private String producerName;

    // 와인명
    private String wine;

    // 생산나라20
    private Country country;

    // 밭
    private String region;

    // 세부 밭
    private String subRegion;

    // 화이트 레드 로제
    private String colour;

    // 타입 - 와인등
    private String type;

    // 서브 타입 - 스파클링 포트 등
    private String subType;

    // 기본 등급
    private String designation;

    // 세부 등급
    private String classification;

    // 와인 이미지 주소
    private String wineImageUrl;

    // 와인 내용
    private String contents;

    // 알콜 도수
    private int alcohol;

    // 최종 토탈 점수
    private int averageScore;

    public WineResponse(Wine entity) {
        this.wineId = entity.getWineId();
        this.displayName = entity.getDisplayName();
        this.displayNameKo = entity.getDisplayNameKo();
        this.grapeList = entity.getGrapeList();
        this.producerName = entity.getProducerName();
        this.wine = entity.getWine();
        this.country = entity.getCountry();
        this.region = entity.getRegion();
        this.subRegion = entity.getSubRegion();
        this.colour = entity.getColour();
        this.type = entity.getType();
        this.subType = entity.getSubType();
        this.designation = entity.getDesignation();
        this.classification = entity.getClassification();
        this.wineImageUrl = entity.getWineImageUrl();
        this.contents = entity.getContents();
        this.alcohol = entity.getAlcohol();
        this.averageScore = entity.getAverageScore();
    }
}
