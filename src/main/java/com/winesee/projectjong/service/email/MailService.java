package com.winesee.projectjong.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javamail;

    public void sendRegisterMail(String username,String email, String authKey, String redisId){

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("Warin 회원가입 인증 메일 입니다.");
        simpleMailMessage.setText("<h1>가입 인증 메일<h1> \n \n" +
                "Hello " + username + " 님 가입해주셔서 감사합니다! \n \n" +
                "아래 링크를 클릭하시면 이메일 인증이 완료됩니다. \n \n" +
                "https://warin.kr/account/email/sign/confirm?email=" + email +
                "&authId=" + redisId +
                "&authKey=" + authKey + "\n \n 서포트팀 매니저");
        javamail.send(simpleMailMessage);
    }

    public void sendFindPassMail(String username,String email, String authKey){

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setTo(email);
        simpleMailMessage.setSubject("Warin 비밀번호 초기화 메일 입니다.");
        simpleMailMessage.setText("<h1>비밀번호 초기화 메일<h1> \n \n" +
                "Hello " + username + " 님의 비밀번호가 초기화 되었습니다. \n \n" +
                "회원님의 임시 비밀번호는 \n \n" +
                "임시 비밀번호 : " + authKey + "\n \n 서포트팀 매니저");
        javamail.send(simpleMailMessage);
    }
}
