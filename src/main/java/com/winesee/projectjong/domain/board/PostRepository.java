package com.winesee.projectjong.domain.board;

import com.winesee.projectjong.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {
    // 유저이름으로 조회후 페이징 처리.f
    Page<Post> findByUsernameOrderByIdDesc(String username, Pageable pageable);
}
