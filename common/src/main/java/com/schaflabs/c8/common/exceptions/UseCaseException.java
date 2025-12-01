package com.schaflabs.c8.common.exceptions;

public class UseCaseException extends RuntimeException {

  private final String userMessage;

  public UseCaseException(String userMessage, String developerMessage) {
    super(developerMessage);
    this.userMessage = userMessage;
  }

  public UseCaseException(String userMessage, String developerMessage, Throwable cause) {
    super(developerMessage, cause);
    this.userMessage = userMessage;
  }

  public String getUserMessage() {
    return userMessage;
  }
}
