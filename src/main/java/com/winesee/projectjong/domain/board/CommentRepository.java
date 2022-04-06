package com.winesee.projectjong.domain.board;

import com.winesee.projectjong.domain.user.User;
import com.winesee.projectjong.domain.wine.Wine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

//    // 전체 조회 (리스트)
    @EntityGraph(attributePaths = {"postId","userId"})
    @Query(value = "SELECT c FROM Comment c WHERE c.postId.postId = ?1")
    Page<Comment> findAllByPostId(Long postId, Pageable pageable);

    @Transactional
    @Modifying
    @Query("delete from Comment c where c.postId.postId = ?1")
    void deleteAllById(Long postId);

    @Transactional
    @Modifying
    @Query("delete from Comment c where c.commentId = ?1 AND c.userId.id = ?2")
    void deleteByIdAndUserId(Long commentId, Long userId);
}
