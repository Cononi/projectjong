package com.winesee.projectjong.service.wine;


import com.winesee.projectjong.controller.User;
import com.winesee.projectjong.domain.util.specification.SearchCriteria;
import com.winesee.projectjong.domain.util.specification.UserSpecificationsBuilder;
import com.winesee.projectjong.domain.util.specification.WineSpecification;
import com.winesee.projectjong.domain.wine.*;
import com.winesee.projectjong.domain.wine.dto.WineResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.security.Escape;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import javax.transaction.Transactional;
import java.lang.reflect.Field;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WineServiceImpl implements WineService {

    private final WineRepository wineRepository;
    private final CountryRepository countryRepository;

    @Override
    public Page<WineResponse> wineAll(Search search) throws IllegalAccessException {
        UserSpecificationsBuilder builder = new UserSpecificationsBuilder();
        Field[] fields = search.getClass().getDeclaredFields();
        StringBuilder valueSet = new StringBuilder("");

//        if(search.getAttr() != null) builder.with(search.getAttr(), "contains", search.getQuery());
//        if(search.getColour() != null) builder.with("colour", "contains", search.getColour());
//        if(search.getSubType() != null) builder.with("subType", "contains", search.getSubType());
//        if(search.getAlcohol() != null) builder.with("alcohol", "equals", search.getAlcohol());
//        if(search.getCountry() != null) builder.with("country", "equals", search.getCountry());

        for(Field fld : fields) {
            fld.setAccessible(true);
            if(fld.getName().equals("query") && StringUtils.isBlank(search.getAttr())){
                if(isAlphaNumeric(search.getQuery())) builder.with("displayName", "contains", search.getQuery());
                else builder.with("displayNameKo", "contains", search.getQuery());
            } else if(fld.getName().equals("attr") && StringUtils.isNotBlank(search.getQuery()) && StringUtils.isNotBlank(search.getAttr())) {
                builder.with(search.getAttr(), "contains", search.getQuery());
            } else if(!ObjectUtils.isEmpty(fld.get(search)) && !fld.getName().equals("page")
                    && !fld.getName().equals("keyword") && !fld.getName().equals("query")) {
                builder.with(fld.getName(), "equals", fld.get(search));
            }
            if(!ObjectUtils.isEmpty(fld.get(search))){
                valueSet.append(fld.getName()).append("=").append(fld.get(search)).append("&");
            }
        }
        search.SearchKeyword(valueSet.substring(0,valueSet.lastIndexOf("=")+1));
        Specification<Wine> spec = builder.build();
        Pageable pageable = PageRequest.of(search.getPage()-1,9);
        return wineRepository.findAll(spec,pageable).map(WineResponse::new);
    }

    @Override
    public WineResponse wineGet(Long id) {
        return new WineResponse(wineRepository.findById(id).orElseThrow(() -> new NullPointerException("존재하지 않음")));
    }

    @Override
    public List<Country> wineCountryResult() {
        return countryRepository.findAll();
    }

    // 영문인지 검사.
    private boolean isAlphaNumeric(String str) {
        return Pattern.matches("[a-zA-Z0-9,() ]*$", str);
    }

}
