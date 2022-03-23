package com.winesee.projectjong.domain.board;

import com.winesee.projectjong.domain.wine.Wine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllBywineId(Wine wineId, Pageable pageable);
}
