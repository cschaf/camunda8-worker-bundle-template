package com.schaflabs.c8.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ServiceAdapterResponse<T> {
  private int statusCode;
  private T body;
  private boolean success;
  private String userErrorMessage;
  private String developerErrorMessage;
  private Exception exception;

  // Constructor ohne Exception für Success-Cases
  public ServiceAdapterResponse(int statusCode, String userMessage, T body, boolean success) {
    this.statusCode = statusCode;
    this.body = body;
    this.success = success;
    this.userErrorMessage = userMessage;
    this.developerErrorMessage = null;
    this.exception = null;
  }

  // Factory Method für Error mit Status Code
  public static <T> ServiceAdapterResponse<T> error(
      int statusCode, String userErrorMessage, String developerErrorMessage, Exception exception) {
    ServiceAdapterResponse<T> response = new ServiceAdapterResponse<>();
    response.setSuccess(false);
    response.setStatusCode(statusCode);
    response.setUserErrorMessage(userErrorMessage);
    response.setDeveloperErrorMessage(developerErrorMessage);
    response.setException(exception);
    response.setBody(null);
    return response;
  }

  // Factory Method für Error mit Status Code
  public static <T> ServiceAdapterResponse<T> error(int statusCode, String errorMessage) {
    ServiceAdapterResponse<T> response = new ServiceAdapterResponse<>();
    response.setSuccess(false);
    response.setStatusCode(statusCode);
    response.setUserErrorMessage(errorMessage);
    response.setDeveloperErrorMessage(errorMessage);
    response.setException(null);
    response.setBody(null);
    return response;
  }

  public static <T> ServiceAdapterResponse<T> success(
      int statusCode, String userErrorMessage, T data) {
    ServiceAdapterResponse<T> response = new ServiceAdapterResponse<>();
    response.setSuccess(true);
    response.setStatusCode(statusCode);
    response.setUserErrorMessage(userErrorMessage);
    response.setDeveloperErrorMessage("");
    response.setBody(data);
    response.setException(null);
    return response;
  }
}
