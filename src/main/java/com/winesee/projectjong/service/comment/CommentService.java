package com.winesee.projectjong.service.comment;

import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.board.dto.CommentRequest;
import com.winesee.projectjong.domain.board.dto.CommentResponse;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface CommentService {

    // 작성
    Long createComment(CommentRequest request, UserResponse user);
    // 수정
    Long editComment(CommentRequest request, UserResponse user);
    // 삭제
    Long deleteComment(Long commentId, UserResponse userId);
    // 조회
    Page<CommentResponse> listComment(Long postId, int page);


}
