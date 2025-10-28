package com.schaflabs.c8.workers.usecases;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.schaflabs.c8.common.domain.UseCase;
import com.schaflabs.c8.workers.definitions.CreateExampleRequestDto;
import com.schaflabs.c8.workers.definitions.ExampleObjectDataDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface CreateExampleObjectUseCase
    extends UseCase<CreateExampleObjectUseCase.Response, CreateExampleRequestDto> {

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonIgnoreProperties(ignoreUnknown = true)
  class Response {
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("data")
    private ExampleObjectDataDto data;
  }
}
