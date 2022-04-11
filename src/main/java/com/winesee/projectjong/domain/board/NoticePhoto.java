package com.winesee.projectjong.domain.board;

import com.winesee.projectjong.domain.basedefault.BaseTime;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
public class NoticePhoto extends BaseTime {

    // 사진 번호
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long noticePhotoId;

    // 등록된 사진 경로
    private String homeLocation;

    // 원본 경로
    private String mainLocation;

    @Builder
    public NoticePhoto(String homeLocation, String mainLocation) {
        this.homeLocation = homeLocation;
        this.mainLocation = mainLocation;
    }
}
