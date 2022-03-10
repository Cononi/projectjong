package com.winesee.projectjong.service;

import com.winesee.projectjong.service.email.MailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;

@SpringBootTest
public class MailServiceTest {

    @Autowired
    MailService mailService;

    @Test
    public void testMail(){
        mailService.sendRegisterMail("TestName","ghah0@naver.com","TestCode","TestId");
    }
}
