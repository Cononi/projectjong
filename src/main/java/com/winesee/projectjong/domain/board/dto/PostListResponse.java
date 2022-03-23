package com.winesee.projectjong.domain.board.dto;

import com.winesee.projectjong.domain.board.Comment;
import com.winesee.projectjong.domain.board.Post;
import com.winesee.projectjong.domain.wine.dto.WineResponse;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class PostListResponse {

    // 번호
    private Long postId;

    // 유저 사진

    private String userProfileImgUrl;

    // 작성자
    private String userId;

    // 제목
    private String title;

    // 와인 정보 ( 자동 완성 기능 )

    private Long wineId;

    private LocalDateTime modifieDate;


    public PostListResponse(Post entity) {
        this.postId = entity.getPostId();
        this.userId = entity.getUserId().getName();
        this.title = entity.getTitle();
        this.wineId = entity.getWineId().getWineId();
        this.modifieDate = entity.getModifieDate();
        this.userProfileImgUrl = entity.getUserId().getProfileImageUrl();
    }
}
