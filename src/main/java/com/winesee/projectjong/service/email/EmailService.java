package com.winesee.projectjong.service.email;

import com.sun.mail.smtp.SMTPTransport;
import com.winesee.projectjong.domain.redis.EmailCode;
import com.winesee.projectjong.domain.redis.EmailCodeRepository;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Properties;

import static com.winesee.projectjong.config.constant.EmailConstant.*;
import static javax.mail.Message.RecipientType.*;

@Service
@RequiredArgsConstructor
public class EmailService {

//    private final EmailCodeRepository emailCodeRepository;

    public void sendEmail(String username,String email, String authKey, String redisId, String type) throws MessagingException {
        Message message = createEmail(username, email, authKey, redisId, type);
        SMTPTransport smtpTransport = (SMTPTransport) getEmailSession().getTransport(SIMPLE_MAIL_TRANSFER_PROTOCOL);
        smtpTransport.connect(GMAIL_SMTP_SERVER, USERNAME, PASSWORD);
        smtpTransport.sendMessage(message, message.getAllRecipients());
        smtpTransport.close();
    }

    private Message createEmail(String username,String email, String authKey, String redisId, String type) throws MessagingException {
        Message message = new MimeMessage(getEmailSession());
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipients(TO, InternetAddress.parse(email, false));
        message.setRecipients(CC, InternetAddress.parse(CC_EMAIL, false));
        if(type.equals("newPassword")) {
            message.setSubject(EMAIL_SUBJECT);
            message.setText("Hello " + username + ", \n \n 님의 새로운 임시 비밀번호 : " + "비밀번호 설정 요망" + "\n \n 서포트 매니저");
        } else if(type.equals("newEmailCode")) {
            // 서비스단에서 처리
//            EmailCode emailCode = new EmailCode(email);
//            emailCodeRepository.save(emailCode);
            message.setSubject(EMAIL_SUBJECT_AUTHKEY);
            message.setText("<h1>가입 인증 메일<h1> \n \n" +
                    "Hello " + username + " 님 가입해주셔서 감사합니다! \n \n" +
                    "아래 링크를 클릭하시면 이메일 인증이 완료됩니다. \n \n" +
                    "http://localhost:8081/account/email/sign/confirm?email=" + email +
                    "&authId=" + redisId +
                    "&authKey=" + authKey + "\n \n 서포트팀 매니저");
        }
        message.setSentDate(Timestamp.valueOf(LocalDateTime.now()));
        message.saveChanges();
        return message;
    }

    private Session getEmailSession() {
        Properties properties = System.getProperties();
        properties.put(SMTP_HOST, GMAIL_SMTP_SERVER);
        properties.put(SMTP_AUTH, true);
        properties.put(SMTP_PORT, DEFAULT_PORT);
        properties.put(SMTP_STARTTLS_ENABLE, true);
        properties.put(SMTP_STARTTLS_REQUIRED, true);
        return Session.getInstance(properties, null);
    }



}
