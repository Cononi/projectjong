package com.winesee.projectjong.config;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@JsonFormat
public class HttpResponse implements Serializable {

    private LocalDateTime timeStamp;
    private int httpStatusCode; // 200, 201, 400, 500 번
    private HttpStatus httpStatus; // HTTP 상태 코드
    private String reason; // 상태 코드 이유
    private String message; // 상태 메세지

    @Builder
    public HttpResponse(int httpStatusCode, HttpStatus httpStatus, String reason, String message) {
        this.timeStamp = LocalDateTime.now();
        this.httpStatusCode = httpStatusCode;
        this.httpStatus = httpStatus;
        this.reason = reason;
        this.message = message;
    }
}
