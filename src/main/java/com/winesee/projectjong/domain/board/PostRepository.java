package com.winesee.projectjong.domain.board;

import com.winesee.projectjong.domain.user.User;
import com.winesee.projectjong.domain.wine.Wine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.persistence.NamedEntityGraph;
import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"userId"})
    Page<Post> findAllByWineId(Wine wineId, Pageable pageable);

    @EntityGraph(attributePaths = {"userId"})
    Page<Post> findAllByUserIdAndWineId(User user, Wine wineId, Pageable pageable);

    @EntityGraph(attributePaths = {"userId","wineId","wineId.country"})
    Optional<Post> findByPostId(Long postId);

    @Transactional
    @Modifying
    @Query("delete from Post p where p.postId = ?1 and p.userId.id = ?2")
    void deleteByPostIdAndUserId(Long postId, Long userId);
//    // 내가 작성한 와인 리스트 보여주기
//    @EntityGraph(attributePaths = {"userId","wineId.country"})
//    @Query(value = "SELECT p FROM Post p WHERE p.wineId = (SELECT s.wineId FROM Wine s WHERE p.wineId = s.wineId ORDER BY p.modifieDate LIMIT 100) and p.userId = 3 GROUP BY p.wineId")
//    Page<Post> findDistinctPostFetchJoinByUserId(User user,Pageable pageable);


    // 최근 10일이내에 덧글이 가장 많이 달린 리뷰 리스트
//    @Query(value = "SELECT p.* FROM post p,comment c\n" +
//            "WHERE p.post_id = c.post_id and c.modifie_date >= date_sub(NOW(), INTERVAL 10 day)\n" +
//            "GROUP BY p.post_id\n" +
//            "ORDER BY COUNT(c.post_id) DESC LIMIT 5", nativeQuery = true)
//    @Query(value = "SELECT p FROM Post p,Comment c\n" +
//            "WHERE p.postId = c.postId and c.modifieDate >= date_sub(NOW(), INTERVAL 10 day)\n" +
//            "GROUP BY p.postId\n" +
//            "ORDER BY COUNT(c.postId) DESC LIMIT 5")
//    @Query(value = "SELECT DISTINCT p FROM Post p join fetch p.wineId WHERE p.postId in (SELECT DISTINCT postId FROM Comment GROUP BY postId ORDER BY COUNT(*) DESC)")
    @EntityGraph(attributePaths = {"userId","wineId","wineId.country"})
    @Query(value = "SELECT DISTINCT p FROM Post p, Comment c WHERE p.postId = c.postId GROUP BY c.postId ORDER BY COUNT(c.postId) DESC")
    List<Post> findAllWithCommentGraph(Pageable pageable);
}
