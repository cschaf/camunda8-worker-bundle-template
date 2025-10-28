package com.schaflabs.c8.workers.definitions;

import lombok.Getter;

@Getter
public enum ExceptionType {
  CREATE_EXAMPLE_OBJECT_FAILED_EXCEPTION("CreateExampleObjectFailedException");

  private final String exception;

  ExceptionType(String exception) {
    this.exception = exception;
  }
}
