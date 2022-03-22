package com.winesee.projectjong.service.post;

import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.board.PostRepository;
import com.winesee.projectjong.domain.board.dto.PostRequest;
import com.winesee.projectjong.domain.user.dto.UserRequest;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import com.winesee.projectjong.domain.wine.Wine;
import com.winesee.projectjong.domain.wine.WineRepository;
import com.winesee.projectjong.domain.wine.dto.WineResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final WineRepository wineRepository;

    @Override
    public Page<Post> postListSearch() {
        return null;
    }

    @Override
    public Long postCreate(PostRequest request, UserResponse user) {
        // 포스트 작성
        Long postId = postRepository.save(Post.builder()
                .userId(user)
                .wineId(wineRepository.getById(request.getWineId()))
                .title(request.getTitle())
                .contents(request.getContents())
                .vintage(request.getVintage())
                .alcohol(request.getAlcohol())
                .price(request.getPrice())
                .acidityCount(request.getAcidityCount())
                .bodyCount(request.getBodyCount())
                .sugarCount(request.getSugarCount())
                .score(request.getScore()).build()).getPostId();
        // 와인 스코어 업데이트
        wineRepository.updateScore(request.getWineId());
        return postId;
    }
}
