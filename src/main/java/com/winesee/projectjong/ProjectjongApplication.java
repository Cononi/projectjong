package com.winesee.projectjong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.TimeZone;

import static com.winesee.projectjong.config.constant.FileConstant.USER_FOLDER;


//@ComponentScan()
//@ComponentScan(basePackages = {"com.winesee.projectjong.domain.redis"})
//@EnableCaching
@SpringBootApplication
@EnableJpaAuditing // JPA Auditing 활성화
public class ProjectjongApplication {

	@PostConstruct
	public void started() {
		// timezone UTC 셋팅
		TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
	}

	public static void main(String[] args) {
		SpringApplication.run(ProjectjongApplication.class, args);
		new File(USER_FOLDER).mkdirs();
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
