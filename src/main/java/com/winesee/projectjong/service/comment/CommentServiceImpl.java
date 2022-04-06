package com.winesee.projectjong.service.comment;

import com.winesee.projectjong.domain.board.Comment;
import com.winesee.projectjong.domain.board.CommentRepository;
import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.board.PostRepository;
import com.winesee.projectjong.domain.board.dto.CommentRequest;
import com.winesee.projectjong.domain.board.dto.CommentResponse;
import com.winesee.projectjong.domain.board.dto.PostRequest;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class CommentServiceImpl implements CommentService{

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    @Override
    public Long createComment(CommentRequest request, UserResponse user) {
        commentRepository.save(Comment.builder()
                        .comment(request.getComment())
                        .postId(postRepository.getById(request.getPostId()))
                        .userId(user)
                .build());
        return 1L;
    }

    @Override
    public Long editComment(CommentRequest request, UserResponse user) {
        Comment comment = commentRepository.getById(request.getCommentId());
        comment.update(request.getComment());
        commentRepository.save(comment);
        return 1L;
    }

    @Override
    public Long deleteComment(Long commentId, UserResponse userId) {
        commentRepository.deleteByIdAndUserId(commentId, userId.getId());
        return 1L;
    }

    @Override
    public Page<CommentResponse> listComment(Long postId, int page) {
        Pageable pageable = PageRequest.of(page,5, Sort.by("createDate").descending());
        return commentRepository.findAllByPostId(postId,pageable).map(CommentResponse::new);
    }
}
