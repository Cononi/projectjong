package com.winesee.projectjong.domain.util.specification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class SearchCriteria {
    // 컬럼명
    private String key;
    // 조건
    private String condition;
    // 값
    private Object value;

    public SearchCriteria(String key, String condition, Object value) {
        this.key = key;
        this.condition = condition;
        this.value = value;
    }
}
