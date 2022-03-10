package com.winesee.projectjong.service.post;

import com.winesee.projectjong.domain.board.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl {

    private final PostRepository postRepository;

}
