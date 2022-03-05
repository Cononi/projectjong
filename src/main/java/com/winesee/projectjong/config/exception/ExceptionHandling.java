package com.winesee.projectjong.config.exception;

import com.auth0.jwt.exceptions.TokenExpiredException;
import com.winesee.projectjong.config.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.persistence.NoResultException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class ExceptionHandling implements ErrorController {

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private static final String ACCOUNT_LOCKED = "로그인 횟수가 초과하여 차단되었습니다. 아이디나 비밀번호 찾기를 진행해주시기 바랍니다.";
    private static final String METHOD_IS_NOT_ALLOWED = "해당 요청은 허용되지 않은 요청입니다. '%s' 요청이 맞는지 확인해주십시요.";
    private static final String INTERNAL_SERVER_ERROR_MSG = "요청을 처리하는 동안 오류가 발생했습니다.";
    private static final String INCORRECT_CREDENTIALS = "ID 혹은 비밀번호를 잘못 입력하셨거나 등록되지 않은 회원입니다.";
    private static final String ACCOUNT_DISABLED = "계정이 비활성화 되었습니다. 오류일 경우 관리자에게 문의 바랍니다.";
    private static final String ERROR_PROCESSING_FILE = "파일을 처리하는데 오류가 발생하여 실패하였습니다.";
    private static final String NOT_ENOUGH_PERMISSION = "권한이 충분하지 않습니다.";
    public static final String ERROR_PATH = "/errosr"; // http://server/error

    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<HttpResponse> accountDisabledException(){
        return  createHttpResponse(BAD_REQUEST, ACCOUNT_DISABLED);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String maxUploadSizeExceededException(HttpServletRequest request, HttpServletResponse response, RedirectAttributes rtts) throws IOException {
        String referer = request.getHeader("referer");
        rtts.addFlashAttribute("errorProfileMsg", "해당 사진 용량이 초과 입니다.");
        return "redirect:"+referer;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public String IllegalArgumentException(HttpServletRequest request, RedirectAttributes rtts,Exception e) {
        String referer = request.getHeader("referer");
        rtts.addFlashAttribute("errorProfileMsg", e.getMessage());
        return "redirect:"+referer;
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HttpResponse> badCredentialsException() {
        return createHttpResponse(BAD_REQUEST, INCORRECT_CREDENTIALS);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<HttpResponse> accessDeniedException() {
        return createHttpResponse(FORBIDDEN, NOT_ENOUGH_PERMISSION);
    }

    @ExceptionHandler(LockedException.class)
    public ResponseEntity<HttpResponse> lockedException() {
        return createHttpResponse(UNAUTHORIZED, ACCOUNT_LOCKED);
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<HttpResponse> tokenExpiredException(TokenExpiredException exception) {
        return createHttpResponse(UNAUTHORIZED, exception.getMessage());
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<HttpResponse> emailExistException(EmailExistException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(UsernameExistException.class)
    public ResponseEntity<HttpResponse> usernameExistException(UsernameExistException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(EmailNotFoundException.class)
    public ResponseEntity<HttpResponse> emailNotFoundException(EmailNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<HttpResponse> userNotFoundException(UserNotFoundException exception) {
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

//    @ExceptionHandler(NoHandlerFoundException.class)
//    public ResponseEntity<HttpResponse> noHandlerFoundException(NoHandlerFoundException e) {
//        return createHttpResponse(BAD_REQUEST, "페이지가 존재하지 않습니다.");
//    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<HttpResponse> methodNotSupportedException(HttpRequestMethodNotSupportedException exception) {
        HttpMethod supportedMethod = Objects.requireNonNull(exception.getSupportedHttpMethods()).iterator().next();
        return createHttpResponse(METHOD_NOT_ALLOWED, String.format(METHOD_IS_NOT_ALLOWED, supportedMethod));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpResponse> internalServerErrorException(Exception exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, INTERNAL_SERVER_ERROR_MSG);
    }

    @ExceptionHandler(NotAnImageFileException.class)
    public ResponseEntity<HttpResponse> notAnImageFileException(NotAnImageFileException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(NoResultException.class)
    public ResponseEntity<HttpResponse> notFoundException(NoResultException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(NOT_FOUND, exception.getMessage());
    }

    @ExceptionHandler(IOException.class)
    public ResponseEntity<HttpResponse> iOException(IOException exception) {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(INTERNAL_SERVER_ERROR, ERROR_PROCESSING_FILE);
    }

//    @RequestMapping(ERROR_PATH)
//    public ResponseEntity<HttpResponse> notFound404( ) {
//        return createHttpResponse(NOT_FOUND, "해당 페이지는 잘못된 경로 입니다.");
//    }
//
//    public String getErrorPath() {
//        return ERROR_PATH;
//    }

    /**
     * Http 응답상태와 메세지를 받아와 ResponseEntity로 반환
     * @param httpStatus 응답상태
     * @param message 응답 메세지
     * @return ResponseEntity<HttpResponse>(HttpResponse.builder(), httpStatus)
     */
    private ResponseEntity<HttpResponse> createHttpResponse(HttpStatus httpStatus, String message){
        /*
        getReasonPhrase() http의 응답메세지 문자열을 확인
        toLowerCase() 문자열을 소문자로 변환후 반환.
        toUpperCase() 문자열을 대문자로 변환후 반환.
        빌더의 장점 :
        필요한 데이터만 설정할 수 있음
        유연성을 확보할 수 있음
        가독성을 높일 수 있음
        불변성을 확보할 수 있음
         */
        return new ResponseEntity<>(HttpResponse.builder()
                                    .httpStatusCode(httpStatus.value())
                                    .httpStatus(httpStatus)
                                    .reason(httpStatus.getReasonPhrase().toUpperCase())
                                    .message(message)
                                    .build(), httpStatus);
    }

}
