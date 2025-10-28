package com.schaflabs.c8.common;

import com.schaflabs.c8.common.io.DefaultUseCaseHelper;
import com.schaflabs.c8.common.io.UseCaseHelper;
import io.camunda.zeebe.client.api.JsonMapper;
import io.camunda.zeebe.client.impl.ZeebeObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class CommonConfiguration {

  @Bean
  public UseCaseHelper provideUseCaseHelper() {
    return new DefaultUseCaseHelper();
  }

  @Bean
  public JsonMapper zeebeJsonMapper() {
    return new ZeebeObjectMapper();
  }
}
