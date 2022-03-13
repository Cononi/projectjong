package com.winesee.projectjong.service.wine;

import com.winesee.projectjong.domain.wine.Search;
import com.winesee.projectjong.domain.wine.Wine;
import org.springframework.data.domain.Page;

public interface WineService {

    // 모든 와인 정보 가져오기
    Page<Wine> wineAll(Search search) throws IllegalAccessException;

}
