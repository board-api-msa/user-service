package me.junbyoung.UserService.handler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // 존재하지않는 회원 이메일로 로그인하려할때의 예외처리
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<Object> handleUsernameNotFoundException(UsernameNotFoundException ex, WebRequest request) {
        Map<String, Object> body = buildErrorResponse(ex, HttpStatus.BAD_REQUEST.value(), request);
        return new ResponseEntity<>(body, HttpStatus.BAD_REQUEST);
    }

    // 중복된 이메일로 회원가입하려할때의 예외처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        Map<String, Object> body = buildErrorResponse(ex, HttpStatus.CONFLICT.value(), request);
        return new ResponseEntity<>(body, HttpStatus.CONFLICT);
    }

    // 일반적인 예외 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGeneralException(Exception ex, WebRequest request) {
        Map<String, Object> body = buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR.value(), request);
        return new ResponseEntity<>(body, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, Object> buildErrorResponse(Exception ex, int httpStatus, WebRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", httpStatus);
        body.put("message", ex.getMessage());
        body.put("path", request.getDescription(false).replace("uri=", ""));
        return body;
    }
}

