package com.winesee.projectjong.service.post;

import com.winesee.projectjong.config.exception.NotAnImageFileException;
import com.winesee.projectjong.domain.board.Comment;
import com.winesee.projectjong.domain.board.CommentRepository;
import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.board.PostRepository;
import com.winesee.projectjong.domain.board.dto.PostBestListResponse;
import com.winesee.projectjong.domain.board.dto.PostListResponse;
import com.winesee.projectjong.domain.board.dto.PostRequest;
import com.winesee.projectjong.domain.board.dto.PostResponse;
import com.winesee.projectjong.domain.user.User;
import com.winesee.projectjong.domain.user.UserRepository;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import com.winesee.projectjong.domain.wine.WineRepository;
import com.winesee.projectjong.domain.wine.dto.WineMyPostResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static com.winesee.projectjong.config.constant.FileConstant.*;
import static com.winesee.projectjong.config.constant.FileConstant.FILE_SAVED_IN_FILE_SYSTEM;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.springframework.http.MediaType.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class PostServiceImpl implements PostService {

    private final PostRepository postRepository;
    private final WineRepository wineRepository;
    private final CommentRepository commentRepository;

    @Override
    public Page<PostListResponse> postListSearch(int pageCount, Long wineId) {
        Pageable pageable = PageRequest.of(pageCount,5, Sort.by("modifieDate").descending());
        return postRepository.findAllByWineId(wineRepository.getById(wineId),pageable).map(PostListResponse::new);
    }

    @Override
    public Page<PostListResponse> postMyListSearch(UserResponse user, int pageCount, Long wineId) {
        Pageable pageable = PageRequest.of(pageCount,5, Sort.by("modifieDate").descending());
        return postRepository.findAllByUserIdAndWineId(user,wineRepository.getById(wineId),pageable).map(PostListResponse::new);
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
        return new PostResponse(postRepository.findByPostId(postId).orElseThrow(() -> new NoSuchElementException("존재하지 않습니다.")));
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
        commentRepository.deleteAllById(number);
        postRepository.deleteByPostIdAndUserId(number, user.getId());
    }

    @Override
    public Page<WineMyPostResponse> myPostWineList(UserResponse user, int pageCount) {
        Pageable pageable = PageRequest.of(pageCount,12);
          return wineRepository.findEntityGraphWineIdAndUserId(user,pageable).map(WineMyPostResponse::new);
    }

//    @Override
//    public Page<PostListResponse> myPostInfoList(int pageCount, Long postId) {
//        return null;
//    }


    @Override
    public List<PostBestListResponse> bestPostCommentList() {
        Pageable pageable = PageRequest.of(0,6);
        return postRepository.findAllWithCommentGraph(pageable).stream().map(PostBestListResponse::new).collect(Collectors.toList());
    }


}
