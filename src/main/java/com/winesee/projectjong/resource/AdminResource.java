package com.winesee.projectjong.resource;

import com.winesee.projectjong.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminResource {

    private final UserService userService;

    @GetMapping("users/{pageNum}")
    public ResponseEntity<?> users(@PathVariable("pageNum") int page){
        return new ResponseEntity<>(userService.getUsers(page-1), OK);
    }

}
