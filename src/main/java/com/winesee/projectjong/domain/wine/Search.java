package com.winesee.projectjong.domain.wine;

import lombok.Builder;
import lombok.Getter;

@Getter
public class Search {

    // 이름검색시 쓰이는 필드명
    private String attr;
    // 조건 타입
    private String type;
    // 색상
    private String colour;
    // 종류
    private String subType;
    // 알콜
    private Integer alcohol;
    // 나라
    private Long country;
    // 값 존재하면
    private String query;
    private Integer page;
    private String keyword;

    @Builder
    public Search(String attr, String colour, String subType, Integer alcohol, Long country, String type, String query, Integer page, String keyword) {
        this.attr = attr;
        this.colour = colour;
        this.subType = subType;
        this.alcohol = alcohol;
        this.country = country;
        this.type = type;
        this.query = query;
        this.page = page;
        this.keyword = keyword;
    }

    public void SearchKeyword(String keyword){
        this.keyword = keyword;
    }
}
