package org.kuba.exceptionhandler;

import org.hibernate.exception.ConstraintViolationException;
import org.kuba.exception.UserDataIsNotValidException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class GlobalExceptionHandler {
  @ExceptionHandler(UserDataIsNotValidException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
  public ResponseEntity<String> handleUserDataValidationErrors(UserDataIsNotValidException ex) {
    return new ResponseEntity<>(
        ex.getMessage(), new HttpHeaders(), HttpStatus.UNPROCESSABLE_ENTITY);
  }

  @ExceptionHandler({ConstraintViolationException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<String> handleValidationErrors(RuntimeException ex) {
    return new ResponseEntity<>(ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<Object> handleAllUncaughtException(Exception ex) {
    return new ResponseEntity<>(
        ex.getMessage(), new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }
}
