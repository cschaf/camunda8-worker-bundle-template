package com.schaflabs.c8.common.exceptions;

public class DateConversionException extends Throwable {
  public static final String GIVEN_DATE_COULD_NOT_BE_PARSED = "Given date could not be parsed!";
  public static final String GIVEN_DATE_FORMATS_ARE_NOT_VALID = "Given date formats are not valid!";

  public DateConversionException(String errorMessage) {
    super(errorMessage);
  }
}
