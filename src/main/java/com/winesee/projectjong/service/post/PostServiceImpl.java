package com.winesee.projectjong.service.post;

import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.board.PostRepository;
import com.winesee.projectjong.domain.board.dto.PostListResponse;
import com.winesee.projectjong.domain.board.dto.PostRequest;
import com.winesee.projectjong.domain.board.dto.PostResponse;
import com.winesee.projectjong.domain.user.dto.UserRequest;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import com.winesee.projectjong.domain.wine.Wine;
import com.winesee.projectjong.domain.wine.WineRepository;
import com.winesee.projectjong.domain.wine.dto.WineResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final WineRepository wineRepository;

    @Override
    public Page<PostListResponse> postListSearch(int pageCount, Long wineId) {
        Pageable pageable = PageRequest.of(pageCount,5, Sort.by("modifieDate").descending());
        return postRepository.findAllBywineId(wineRepository.getById(wineId),pageable).map(PostListResponse::new);
    }

    @Override
    public Long postCreate(PostRequest request, UserResponse user) {
        // 포스트 작성
        Long postId = postRepository.save(Post.builder()
                .userId(user)
                .wineId(wineRepository.getById(request.getWinePostData()))
                .title(request.getTitPostData())
                .contents(request.getConPostData())
                .vintage(request.getVinPostData())
                .alcohol(request.getAciPostData())
                .price(request.getPriPostData())
                .acidityCount(request.getAciPostData())
                .bodyCount(request.getBodyPostData())
                .sugarCount(request.getSugPostData())
                .score(request.getScPostData()).build()).getPostId();
        // 와인 스코어 업데이트
        wineRepository.updateScore(request.getWinePostData());
        return postId;
    }

    @Override
    public PostResponse postGet(Long postId) {
        return new PostResponse(postRepository.findById(postId).orElseThrow(() -> new NoSuchElementException("존재하지 않습니다.")));
    }

    @Override
    public Long postEdit(PostRequest request, UserResponse user) {
        Post getPost = postRepository.getById(request.getIdPostData());
        if(getPost.getUserId().getId().equals(user.getId())){
            getPost.Update(request.getTitPostData(),
                    request.getVinPostData(),
                    request.getBodyPostData(),
                    request.getSugPostData(),
                    request.getAciPostData(),
                    request.getPriPostData(),
                    request.getAlcPostData(),
                    request.getScPostData(),
                    request.getConPostData());
            return postRepository.save(getPost).getPostId();
        }
        return null;
    }

    @Override
    public void postDelete(Long number, UserResponse user) {
        Post post = postRepository.getById(number);
        if(post.getUserId().getId().equals(user.getId())){
            postRepository.deleteById(number);
        }
    }
}
