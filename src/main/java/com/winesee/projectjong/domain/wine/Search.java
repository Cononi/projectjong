package com.winesee.projectjong.domain.wine;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class Search {

    private String attr;
    private String type;
    private String query;
    private Integer page;

    @Builder
    public Search(String attr, String type, String query, Integer page) {
        this.attr = attr;
        this.type = type;
        this.query = query;
        this.page = page;
    }
}
