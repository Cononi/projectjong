package com.winesee.projectjong.domain.board;

import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    // 전체 조회 (리스트)
    // 단일 조회 (수정)

}
