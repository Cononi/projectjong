package com.winesee.projectjong.domain.board;

import com.winesee.projectjong.domain.basedefault.BaseTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Notice extends BaseTime {

    // 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticeId;

    // 타이틀
    private String noticeTitle;

    // 콘텐츠
    @Lob
    private String noticeContents;

    @Builder
    public Notice(String noticeTitle, String noticeContents) {
        this.noticeTitle = noticeTitle;
        this.noticeContents = noticeContents;
    }
}
