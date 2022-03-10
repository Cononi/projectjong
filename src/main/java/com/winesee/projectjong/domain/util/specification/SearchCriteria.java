package com.winesee.projectjong.domain.util.specification;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class SearchCriteria {
    // 컬럼명
    private String key;
    // 조건
    private String condition;
    // 값
    private Object value;
}
