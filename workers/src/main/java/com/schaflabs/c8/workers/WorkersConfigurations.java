package com.schaflabs.c8.workers;

import com.schaflabs.c8.workers.io.ApiRestTemplate;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class WorkersConfigurations {

  @Bean
  public ApiRestTemplate provideApiRestTemplate() {
    // Vereinfacht - RequestFactory wird jetzt in ApiRestTemplate erstellt
    return new ApiRestTemplate(null);
  }
}
