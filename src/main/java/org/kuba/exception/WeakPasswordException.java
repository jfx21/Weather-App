package org.kuba.exception;

public class WeakPasswordException extends RuntimeException {
  public WeakPasswordException(String message) {
    super(message);
  }
}
