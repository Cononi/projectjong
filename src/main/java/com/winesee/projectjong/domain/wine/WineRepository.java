package com.winesee.projectjong.domain.wine;

import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.user.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;


public interface WineRepository extends JpaRepository<Wine, Long>, JpaSpecificationExecutor<Wine> {

    @EntityGraph(attributePaths = {"country"})
    Page<Wine> findAll(Specification specification, Pageable pageable);

    @EntityGraph(attributePaths = {"country"})
    Wine findByWineId(Long id);

    // 와인 업데이트
    @Transactional
    @Modifying
    @Query(
            value = "UPDATE wine SET average_score = (SELECT SUM(score)/COUNT(wine_id) FROM post where wine_id=?1) WHERE wine_id = ?1",
            nativeQuery = true)
    void updateScore(Long wineId);

    // 내가 작성한 와인 리스트 보여주기
    @EntityGraph(attributePaths = {"country"})
    @Query("SELECT DISTINCT c FROM Wine c, Post p WHERE p.modifieDate in (Select max(s.modifieDate) FROM Post s where s.wineId=p.wineId) and p.wineId = c.wineId and p.userId = :user ORDER BY p.modifieDate DESC")
    Page<Wine> findEntityGraphWineIdAndUserId(@Param("user") User userId,Pageable pageable);


}
