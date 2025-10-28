package com.schaflabs.c8.workers.io;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class ApiRestTemplate extends RestTemplate {
  public ApiRestTemplate(RestTemplate source) {
    super(new HttpComponentsClientHttpRequestFactory());
    if (source == null) {
      return;
    }
    this.setMessageConverters(source.getMessageConverters());
    this.setClientHttpRequestInitializers(source.getClientHttpRequestInitializers());
    this.setErrorHandler(source.getErrorHandler());
    this.setUriTemplateHandler(source.getUriTemplateHandler());
    this.setInterceptors(source.getInterceptors());
    this.setRequestFactory(source.getRequestFactory());
  }
}
