package com.winesee.projectjong.domain.wine;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;


public interface WineRepository extends JpaRepository<Wine, Long>, JpaSpecificationExecutor<Wine> {

    Page<Wine> findAll(Specification specification, Pageable pageable);

    @Modifying
    @Query(
            value = "UPDATE wine SET average_score = (SELECT SUM(score)/COUNT(wine_id) FROM post where wine_id=?1) WHERE wine_id = ?1",
            nativeQuery = true)
    void updateScore(Long wineId);

}
