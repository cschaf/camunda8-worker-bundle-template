package com.schaflabs.c8.workers.usecases;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.schaflabs.c8.common.domain.UseCase;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface CreateExampleObjectUseCase
    extends UseCase<CreateExampleObjectUseCase.Response, CreateExampleObjectUseCase.Request> {

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Request {

    @JsonProperty("name")
    private String name;

    @JsonProperty("data")
    private ObjectDataDto data;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class Response {
    @JsonProperty("id")
    private String id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("createdAt")
    private String createdAt;

    @JsonProperty("data")
    private ObjectDataDto data;
  }

  @Data
  @AllArgsConstructor
  @NoArgsConstructor
  @Builder
  @JsonInclude(JsonInclude.Include.NON_NULL)
  @JsonIgnoreProperties(ignoreUnknown = true)
  public static class ObjectDataDto {
    @JsonProperty("year")
    private String year;

    @JsonProperty("price")
    private String price;

    @JsonProperty("CPU model")
    private String cpuModel;

    @JsonProperty("Hard disk size")
    private String hardDiskSize;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("color")
    private String color;
  }
}
