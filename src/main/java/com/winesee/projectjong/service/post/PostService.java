package com.winesee.projectjong.service.post;


import com.winesee.projectjong.domain.board.dto.PostListResponse;
import com.winesee.projectjong.domain.board.dto.PostRequest;
import com.winesee.projectjong.domain.board.dto.PostResponse;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import com.winesee.projectjong.domain.wine.dto.WineMyPostResponse;
import com.winesee.projectjong.domain.wine.dto.WineResponse;
import org.springframework.data.domain.Page;

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

    // 내가 작성한 와인 리스트 보여주기
    Page<WineMyPostResponse> myPostWineList(UserResponse user, int pageCount);

    // 내가 작성한 와인의 글 목록 보여주기
    Page<PostListResponse> myPostInfoList(int pageCount, Long postId);
}
