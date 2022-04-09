package com.winesee.projectjong.controller;

import com.winesee.projectjong.domain.board.PostRepository;
import com.winesee.projectjong.domain.board.dto.PostResponse;
import com.winesee.projectjong.domain.user.dto.UserResponse;
import com.winesee.projectjong.domain.wine.WineRepository;
import com.winesee.projectjong.domain.wine.dto.WineRequest;
import com.winesee.projectjong.domain.wine.dto.WineResponse;
import com.winesee.projectjong.service.comment.CommentService;
import com.winesee.projectjong.service.post.PostService;
import com.winesee.projectjong.service.wine.WineService;
import io.lettuce.core.dynamic.annotation.Param;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequiredArgsConstructor
@RequestMapping("/")
@Slf4j
public class Post {

    private final WineService wineService;
    private final PostService postService;

    @GetMapping("post/{number}")
    public String post(Model model, @PathVariable("number") Long number, HttpServletRequest request){
        // 와인 정보 가져옴.
        WineResponse wine = wineService.wineGet(number);
        // 와인정보 뿌림.
        model.addAttribute("wineInfo", wine);
        return "pages/post/post";
    }

    @GetMapping("post/edit/{number}")
    public String edit(Model model, @AuthenticationPrincipal UserResponse userinfo, @PathVariable("number") Long number){
        // 와인 정보 가져옴.
        PostResponse post = postService.postGet(number);
        if(userinfo.getId().equals(post.getUserNum())){
            // 와인정보 뿌림.
            model.addAttribute("postInfo", post);
            model.addAttribute("wineInfo",post.getWineId());
            return "pages/post/edit";
        } else {
            return "redirect:/";
        }
    }


    @GetMapping("post/info/{number}/{usePage}")
    public String info(@AuthenticationPrincipal UserResponse userinfo, Model model, HttpServletRequest request, @PathVariable("number") Long number, @PathVariable("usePage") Long usePage){
        String referer = (String)request.getHeader("REFERER");
        PostResponse post = postService.postGet(number);
        String backLink ="";

        // 이전 페이지 정보를 가져옴.
        if(referer != null){
            if(referer.contains("wine/")) {
                backLink = referer.substring(0,referer.lastIndexOf("/"))+"/"+usePage;
            } else if(referer.contains("account/tasting")) {
                backLink = "/account/tasting";
            } else {
            // 만약 널은 아니지만 사용자가 동일 페이지에서 리다이렉트 했을경우.
                backLink = "/wine/1/" + post.getWineId().getWineId() + "/" + usePage;
            }
        } else {
            // 만약 사용자가 동일 페이지에서 리다이렉트 했을경우.
            backLink = "/wine/1/" + post.getWineId().getWineId() + "/" + usePage;
        }
        model.addAttribute("backLink",backLink);
        // 포스트 정보
        model.addAttribute("postInfo", post);
        return "pages/post/postinfo";
    }

    @GetMapping("account/post/list")
    public String postList(@AuthenticationPrincipal UserResponse userinfo){
        return "pages/mypage/mypostlist";
    }
}
