package com.winesee.projectjong.service.post;


import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.board.dto.PostListResponse;
import com.winesee.projectjong.domain.board.dto.PostRequest;
import com.winesee.projectjong.domain.board.dto.PostResponse;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface PostService {

    // 게시글 목록 가져오기 AllList
    Page<PostListResponse> postListSearch(int pageCount, Long wineId);

    // 글 보여주기
    PostResponse postGet(Long postId);
    // 글 작성
    Long postCreate(PostRequest request, UserResponse user);
    // 글 수정
    Long postEdit(PostRequest request, UserResponse user);
    // 글 삭제
    void postDelete(Long number, UserResponse user);

}
