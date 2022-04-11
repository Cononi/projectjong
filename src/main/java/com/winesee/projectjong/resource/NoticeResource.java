package com.winesee.projectjong.resource;

import com.winesee.projectjong.domain.board.dto.NoticeRequest;
import com.winesee.projectjong.service.post.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import static com.winesee.projectjong.config.constant.FileConstant.*;
import static org.springframework.http.MediaType.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class NoticeResource {

    private final NoticeService noticeService;

    // 리스트 목록
    @GetMapping(value = "v1/notice/{number}")
    public ResponseEntity<?> noticeListAPI(@PathVariable("number") int pageNum) {
        return new ResponseEntity<>(noticeService.noticeList(pageNum-1,10), HttpStatus.OK);
    }

    // 공지 작성
    @PostMapping(value = "v1/notice/post", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<?> noticeCreate(@RequestBody NoticeRequest request) {
        return new ResponseEntity<>(noticeService.CreateNotice(request), HttpStatus.OK);
    }

    // 이미지 업로드
    @PostMapping(value = "v1/notice/image/upload",  produces = {IMAGE_PNG_VALUE,IMAGE_JPEG_VALUE})
    public String photoImageUpload(@RequestParam(value = "multipartFiles", required = false) MultipartFile noticeImage) throws IOException {
        log.info(noticeService.saveNoticeImage(noticeImage));
        return noticeService.saveNoticeImage(noticeImage);
    }

    // 이미지 가져오기
    @GetMapping(path = "v1/notice/image/{folder}/{fileName}", produces = {IMAGE_PNG_VALUE,IMAGE_JPEG_VALUE})
    public byte[] getProfileImage(@PathVariable("folder") String folder, @PathVariable("fileName") String fileName) throws IOException {
        return Files.readAllBytes(Paths.get(NOTICE_FOLDER + folder + FORWARD_SLASH + fileName + DOT + PNG_EXTENSION));
    }

}
