package com.winesee.projectjong.service.post;

import com.winesee.projectjong.domain.board.dto.NoticeRequest;
import com.winesee.projectjong.domain.board.dto.NoticeResponse;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface NoticeService {

    // 공지 작성
    Long CreateNotice(NoticeRequest request);

    // 공지 글 하나
    NoticeResponse noticeGet(Long noticeId);

    // 공지 리스트
    Page<NoticeResponse> noticeList(int page, int size);

    // 공지 삭제

    // 공지 수정

    // 이미지 등록
    String saveNoticeImage(MultipartFile profileImage) throws IOException;
}
