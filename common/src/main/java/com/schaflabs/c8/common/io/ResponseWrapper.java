package com.schaflabs.c8.common.io;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;

import java.util.Date;

/**
 * Diese Klasse ist die einzig gültige REST Response.
 *
 * @param <T> Der Typ von {@link ResponseWrapper#data}
 */
@Builder
@AllArgsConstructor
@Data
public class ResponseWrapper<T> {
  private final T data;
  private final int statusCode;
  private final long internalCode;
  private final String developerMessage;
  private final String userMessage;
  private final Date timestamp;
  private final boolean isSuccessful;
  private final transient HttpHeaders headers;

  public ResponseEntity toResponseEntity() {
    if (this.headers != null) {
      return ResponseEntity.status(statusCode).headers(headers).body(data);
    }
    return ResponseEntity.status(statusCode).body(this);
  }
}
