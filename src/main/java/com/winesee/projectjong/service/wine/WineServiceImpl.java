package com.winesee.projectjong.service.wine;


import com.winesee.projectjong.domain.util.specification.SearchCriteria;
import com.winesee.projectjong.domain.util.specification.WineSpecification;
import com.winesee.projectjong.domain.wine.Search;
import com.winesee.projectjong.domain.wine.Wine;
import com.winesee.projectjong.domain.wine.WineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WineServiceImpl implements WineService {

    private final WineRepository wineRepository;

    @Override
    public Page<Wine> wineAll(Search search) {
        WineSpecification spec =
                new WineSpecification(new SearchCriteria(search.getAttr(), search.getType(), search.getQuery()));
        Pageable pageable = PageRequest.of(search.getPage()-1,9, Sort.Direction.DESC, search.getAttr());
        return wineRepository.findAll(spec,pageable);
    }



}
