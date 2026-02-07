package com.schaflabs.c8.workers.io;

import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

public class ApiRestTemplate extends RestTemplate {
  public ApiRestTemplate(RestTemplate source) {
    // NEU: Verwende HttpComponentsClientHttpRequestFactory mit HttpClient 5
    super(createRequestFactory());

    if (source == null) {
      return;
    }

    this.setMessageConverters(source.getMessageConverters());
    this.setClientHttpRequestInitializers(source.getClientHttpRequestInitializers());
    this.setErrorHandler(source.getErrorHandler());
    this.setUriTemplateHandler(source.getUriTemplateHandler());
    this.setInterceptors(source.getInterceptors());
  }

  private static HttpComponentsClientHttpRequestFactory createRequestFactory() {
    HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
    factory.setConnectTimeout(5000); // 5 Sekunden Connection Timeout
    factory.setConnectionRequestTimeout(5000); // 5 Sekunden Request Timeout
    return factory;
  }
}
