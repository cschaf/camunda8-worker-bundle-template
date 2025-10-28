package com.schaflabs.c8.workers;

import com.schaflabs.c8.workers.io.ApiRestTemplate;
import com.schaflabs.c8.workers.usecases.CreateExampleObjectUseCase;
import com.schaflabs.c8.workers.usecases.CreateExampleObjectUseCaseImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;

@Configuration
@RequiredArgsConstructor
public class WorkersConfigurations {

  @Bean
  public ApiRestTemplate provideApiRestTemplate() {
    ApiRestTemplate restTemplate = new ApiRestTemplate(null);
    restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
    return restTemplate;
  }

  @Bean
  public CreateExampleObjectUseCase provideCreateExampleObjectUseCase() {
    return new CreateExampleObjectUseCaseImpl();
  }
}
