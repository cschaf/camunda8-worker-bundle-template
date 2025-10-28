package com.schaflabs.c8.workers.io;

import com.schaflabs.c8.common.domain.ServiceAdapterResponse;
import com.schaflabs.c8.workers.configurations.CommonProperties;
import com.schaflabs.c8.workers.configurations.ObjectProperties;
import com.schaflabs.c8.workers.definitions.CreateExampleRequestDto;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@AllArgsConstructor
@Log4j2
public class ServiceAdapterRest implements ServiceAdapter {
  private final CommonProperties commonProperties;
  private final ObjectProperties objectProperties;
  private final ApiRestTemplate restTemplate;

  @Override
  public <T> ServiceAdapterResponse<T> createExampleObject(
      CreateExampleRequestDto request, Class<T> clazz) {

    String url =
        UriComponentsBuilder.newInstance()
            .scheme(commonProperties.getScheme())
            .host(commonProperties.getHost())
            .path(objectProperties.getCreateObjectRoute())
            .build()
            .toString();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);
    ResponseEntity<T> response = null;
    try {
      response =
          restTemplate.exchange(url, HttpMethod.POST, new HttpEntity(request, headers), clazz);
    } catch (HttpServerErrorException | HttpClientErrorException e) {
      return ServiceAdapterResponse.error(
          e.getStatusCode().value(),
          "Something went wrong while creating example object",
          e.getMessage(),
          e);
    }
    if (response.getStatusCode().is2xxSuccessful()) {
      return ServiceAdapterResponse.success(
          response.getStatusCode().value(),
          "Successfully created an example object",
          response.getBody());
    }
    return ServiceAdapterResponse.error(
        response.getStatusCode().value(), "Something went wrong while creating example object");
  }

  @Override
  public <T> ServiceAdapterResponse<T> getExampleObjectById(long id, Class<T> clazz) {
    return null;
  }

  @Override
  public <T> ServiceAdapterResponse<List<T>> getAllExampleObjects(Class<T> clazz) {
    return null;
  }

  @Override
  public <T> ServiceAdapterResponse<T> deleteExampleObjectById(long id, Class<T> clazz) {
    return null;
  }

  @Override
  public <T> ServiceAdapterResponse<T> updateExampleObjectById(long id, Class<T> clazz) {
    return null;
  }
}
