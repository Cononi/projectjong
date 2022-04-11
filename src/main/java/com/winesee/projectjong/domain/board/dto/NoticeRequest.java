package com.winesee.projectjong.domain.board.dto;

import com.winesee.projectjong.domain.board.Notice;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class NoticeRequest {

    // 타이틀
    private String noticeTitle;

    // 콘텐츠
    private String noticeContents;


    public Notice toEntity() {
        return Notice.builder()
                .noticeTitle(noticeTitle)
                .noticeContents(noticeContents)
                .build();
    }
}
