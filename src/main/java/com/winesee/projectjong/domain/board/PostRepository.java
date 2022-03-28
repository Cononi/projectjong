package com.winesee.projectjong.domain.board;

import com.winesee.projectjong.domain.user.User;
import com.winesee.projectjong.domain.wine.Wine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllBywineId(Wine wineId, Pageable pageable);

//    // 내가 작성한 와인 리스트 보여주기
//    @EntityGraph(attributePaths = {"userId","wineId.country"})
//    @Query(value = "SELECT p FROM Post p WHERE p.wineId = (SELECT s.wineId FROM Wine s WHERE p.wineId = s.wineId ORDER BY p.modifieDate LIMIT 100) and p.userId = 3 GROUP BY p.wineId")
//    Page<Post> findDistinctPostFetchJoinByUserId(User user,Pageable pageable);
}
