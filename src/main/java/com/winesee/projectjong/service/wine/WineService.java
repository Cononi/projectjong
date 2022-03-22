package com.winesee.projectjong.service.wine;

import com.winesee.projectjong.domain.wine.Country;
import com.winesee.projectjong.domain.wine.Search;
import com.winesee.projectjong.domain.wine.Wine;
import com.winesee.projectjong.domain.wine.dto.WineResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface WineService {

    // 모든 와인 정보 가져오기
    Page<WineResponse> wineAll(Search search) throws IllegalAccessException;

    WineResponse wineGet(Long id);

    List<Country> wineCountryResult();

}
