package com.winesee.projectjong.domain.wine;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WineRepository extends JpaRepository<Wine, Long> {

    Page<Wine> findAll(Pageable pageable);

}
