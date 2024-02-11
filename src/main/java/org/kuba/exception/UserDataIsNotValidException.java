package org.kuba.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class UserDataIsNotValidException extends RuntimeException {
  public UserDataIsNotValidException(String validationResult) {
    super(validationResult);
  }
}
