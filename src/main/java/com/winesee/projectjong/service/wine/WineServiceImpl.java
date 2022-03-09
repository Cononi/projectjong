package com.winesee.projectjong.service.wine;


import com.winesee.projectjong.domain.wine.Wine;
import com.winesee.projectjong.domain.wine.WineRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class WineServiceImpl implements WineService {

    private final WineRepository wineRepository;

    @Override
    public Page<Wine> wineAll(Integer pages) {
        Pageable pageable = PageRequest.of(pages-1,9, Sort.Direction.DESC, "displayName");
        return wineRepository.findAll(pageable);
    }


}
