package ru.bereshs.HHWorkSearch.controller.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.bereshs.HHWorkSearch.domain.dto.ExceptionResponse;

import java.time.LocalDateTime;
import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class RestExceptionController {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> exceptionHandler(Exception ex) {
        log.error("trow rest error: ", ex);
        return ResponseEntity.badRequest().body(
                ExceptionResponse.builder()
                        .message(ex.getMessage())
                        .time(LocalDateTime.now())
                        .build());
    }
}
