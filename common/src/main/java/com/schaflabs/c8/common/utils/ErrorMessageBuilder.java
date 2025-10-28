package com.schaflabs.c8.common.utils;

public class ErrorMessageBuilder {

  public static String buildDeveloperErrorMessage(String errorMessage, String exceptionType) {
    return new StringBuilder()
        .append(errorMessage)
        .append(System.lineSeparator())
        .append(String.format("Exception type: %s", exceptionType))
        .toString();
  }
}
