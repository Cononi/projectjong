package com.winesee.projectjong.controller;

import com.winesee.projectjong.config.exception.UserNotFoundException;
import com.winesee.projectjong.domain.user.dto.UserAdminEditRequest;
import com.winesee.projectjong.domain.user.dto.UserRequest;
import com.winesee.projectjong.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/")
@RequiredArgsConstructor
@Slf4j
public class Admin {

    private final UserService userService;

    // 메인 (사용자 상태 이용현황, 금일 접속자, 금일 작성글 개수, 금일 클릭횟수 같은것들)
    @GetMapping("admin")
    public String adminMainPage(){
        return "pages/admin/index";
    }



    // 회원 관리(회원 정보(모달창으로 회원 등급, 회원 탈퇴, 회원 정지, 회원 차단))
    @GetMapping("admin/users")
    public String adminUsersPage(){

        return "pages/admin/users";
    }

    // 유저 상세 정보
    @GetMapping("admin/users/{id}")
    public String adminUsersPage(@PathVariable("id") Long userId, Model model) throws UserNotFoundException {
        model.addAttribute("userInfo", userService.getUserId(userId));
        return "pages/admin/user/userInfo";
    }

    // 유저 상세 정보수정
    @PostMapping("admin/users/edit/{userId}")
    public String adminUserEdit(@PathVariable("userId") Long userId, UserAdminEditRequest request, Model model) throws UserNotFoundException {
        userService.adminPageUserEdit(request, userId);
        return "redirect:/admin/users/" + userId;
    }


    // 작성 글 관리 (삭제, 조회, 수정, 추가)
    @GetMapping("admin/notes")
    public String adminNoteListPage(){
        return "pages/admin/notes";
    }

    // 공지관리 (삭제, 조회, 수정, 추가)
    @GetMapping("admin/notice")
    public String adminNoticePage(Model model){
        return "pages/admin/notice";
    }

    // 메인 배너 관리 ( 메인 배너 정보 변경 - 이미지 업로드 및 링크 설정, 서브타이틀, 메인타이틀, 배너 광고 연결된 글 링크) - Repository Entity 생성 필요.
    @GetMapping("admin/banner")
    public String adminBannerPage(){
        return "pages/admin/banner";
    }
}
