package com.codeit.hrbank.exception;

import java.io.FileNotFoundException;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  // 전역 예외 처리
  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception e) {
    // 500 Internal Server Error
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage() + "오류");
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
    // 400 Bad Request
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage() + "오류");
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ResponseEntity<String> handleNoSuchElementException(NoSuchElementException e) {
    // 404 Not Found
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage() + "오류");
  }

  @ExceptionHandler(FileNotFoundException.class)
  public ResponseEntity<String> handleFileNotFoundException(FileNotFoundException e) {
    // 404 Not Found
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage() + "오류");
  }
}
