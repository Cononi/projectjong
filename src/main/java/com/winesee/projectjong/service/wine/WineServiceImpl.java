package com.winesee.projectjong.service.wine;


import com.winesee.projectjong.controller.User;
import com.winesee.projectjong.domain.util.specification.SearchCriteria;
import com.winesee.projectjong.domain.util.specification.UserSpecificationsBuilder;
import com.winesee.projectjong.domain.util.specification.WineSpecification;
import com.winesee.projectjong.domain.wine.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class WineServiceImpl implements WineService {

    private final WineRepository wineRepository;
    private final CountryRepository countryRepository;

    @Override
    public Page<Wine> wineAll(Search search) throws IllegalAccessException {
        UserSpecificationsBuilder builder = new UserSpecificationsBuilder();
        Field[] fields = search.getClass().getDeclaredFields();
        StringBuilder valueSet = new StringBuilder("");
        for(Field fld : fields) {
            fld.setAccessible(true);
            if(fld.getName().equals("attr") && !ObjectUtils.isEmpty(fld.get(search))) {
                if(ObjectUtils.isEmpty(search.getQuery())){
                    builder.with(search.getAttr(), search.getType(), "");
                }else {
                    builder.with(search.getAttr(), search.getType(), search.getQuery());
                }
            } else if (fld.getName().equals("country") && !ObjectUtils.isEmpty(fld.get(search))){
                builder.with(fld.getName(), "equals", fld.get(search));
            } else if (fld.getName().equals("alcohol") && !ObjectUtils.isEmpty(fld.get(search))) {
                builder.with(fld.getName(), "equals", fld.get(search));
//            } else if (fld.getName().equals("type") && !ObjectUtils.isEmpty(fld.get(search))) {
//                builder.with("displayName", search.getType(), "");
            } else if(!fld.getName().equals("type") && !fld.getName().equals("query") && !fld.getName().equals("alcohol")
                    && !fld.getName().equals("country") && !fld.getName().equals("page") && !fld.getName().equals("keyword")){
                if(!ObjectUtils.isEmpty(fld.get(search))){
                    builder.with(fld.getName(), search.getType(), fld.get(search));
                }
            }
            if(!ObjectUtils.isEmpty(fld.get(search))){
                valueSet.append(fld.getName()).append("=").append(fld.get(search)).append("&");
            }
        }
        search.SearchKeyword(valueSet.substring(0,valueSet.lastIndexOf("=")+1));
        log.info("알려진 문자 : " + search.getKeyword());
//        UserSpecificationsBuilder builder = new UserSpecificationsBuilder();
//        Pattern pattern = Pattern.compile("(\\w+?)(=)(\\w+?)&");
//        Matcher matcher = pattern.matcher(search.getAttr() + "&");
        Specification<Wine> spec = builder.build();
//        WineSpecification spec =
//                new WineSpecification(new SearchCriteria());
        Pageable pageable = PageRequest.of(search.getPage()-1,9);
        return wineRepository.findAll(spec,pageable);
    }

    @Override
    public List<Country> wineCountryResult() {
        return countryRepository.findAll();
    }
}
