package com.winesee.projectjong.service.post;

import com.winesee.projectjong.domain.board.NoticePhoto;
import com.winesee.projectjong.domain.board.NoticePhotoRepository;
import com.winesee.projectjong.domain.board.NoticeRepository;
import com.winesee.projectjong.domain.board.dto.NoticeRequest;
import com.winesee.projectjong.domain.board.dto.NoticeResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static com.winesee.projectjong.config.constant.FileConstant.*;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class NoticeServiceImpl implements NoticeService {

    private final NoticeRepository noticeRepository;
    private final NoticePhotoRepository noticePhotoRepository;

    @Override
    public Long CreateNotice(NoticeRequest request) {
        return noticeRepository.save(request.toEntity()).getNoticeId();
    }

    @Override
    public NoticeResponse noticeGet(Long noticeId) {
        return new NoticeResponse(noticeRepository.getById(noticeId));
    }

    @Override
    public Page<NoticeResponse> noticeList(int page, int size) {
        Pageable pageable = PageRequest.of(page,size, Sort.by("modifieDate").descending());
        return noticeRepository.findAll(pageable).map(NoticeResponse::new);
    }

    @Override
    public String saveNoticeImage(MultipartFile profileImage) throws IOException {
        String url = "";
        if (StringUtils.isNotBlank(profileImage.getOriginalFilename())) { // user/home/warine/user/rick
            // ?????? ?????? ?????????
            LocalDateTime now = LocalDateTime.now();
            // ??????
            String formatedNow = now.format(formatDate("yyyyMMdd"));
            // ?????? ??????
            String formatedNowTo = now.format(formatDate("hhmmssns"));
            // ?????? ??????
            Path userFolder = Paths.get(NOTICE_FOLDER + formatedNow);
            // ????????? ????????? ?????????.
            if(!Files.exists(userFolder)) {
                // ?????? ??????
                Files.createDirectories(userFolder);
                log.info(DIRECTORY_CREATED + userFolder);
            }
            Files.copy(profileImage.getInputStream(), userFolder.resolve(formatedNowTo + DOT + PNG_EXTENSION), REPLACE_EXISTING);
            log.info(FILE_SAVED_IN_FILE_SYSTEM + profileImage.getOriginalFilename());
            url = "/api/v1/notice/image/" + formatedNow + "/" + formatedNowTo;
            noticePhotoRepository.save(NoticePhoto.builder()
                            .homeLocation(url)
                            .mainLocation(NOTICE_FOLDER + formatedNow + "/" + formatedNowTo)
                    .build());
        }
        return url;
    }

    private DateTimeFormatter formatDate(String pattern){
        return DateTimeFormatter.ofPattern(pattern);
    }
}
