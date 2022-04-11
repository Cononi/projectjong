package com.winesee.projectjong.domain.board.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.winesee.projectjong.domain.board.Notice;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NoticeResponse {

    private Long noticeId;

    // 타이틀
    private String noticeTitle;

    // 콘텐츠
    private String noticeContents;

    // 시간
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDateTime modifieDate;


    public NoticeResponse(Notice entity){
        this.noticeId = entity.getNoticeId();
        this.noticeTitle = entity.getNoticeTitle();
        this.noticeContents = entity.getNoticeContents();
        this.modifieDate = entity.getModifieDate();
    }
}
