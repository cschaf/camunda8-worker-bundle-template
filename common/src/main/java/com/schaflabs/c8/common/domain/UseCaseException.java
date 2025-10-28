package com.schaflabs.c8.common.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@Data
@ToString
public class UseCaseException extends Throwable {

  private String userMessage;
  private String developerMessage;
  private int statusCode;
  private Class<?> useCaseType;

  public UseCaseException(String message, Class<?> useCaseType) {
    super(message);

    this.userMessage = message;
    this.developerMessage = message;
    this.statusCode = 500;
    this.useCaseType = useCaseType;
  }

  public UseCaseException(String userMessage, String developerMessage, Class<?> useCaseType) {
    super(developerMessage);

    this.userMessage = userMessage;
    this.developerMessage = developerMessage;
    this.statusCode = 500;
    this.useCaseType = useCaseType;
  }

  public UseCaseException(
      String userMessage, String developerMessage, int statusCode, Class<?> useCaseType) {
    super(developerMessage);

    this.userMessage = userMessage;
    this.developerMessage = developerMessage;
    this.statusCode = statusCode;
    this.useCaseType = useCaseType;
  }
}
