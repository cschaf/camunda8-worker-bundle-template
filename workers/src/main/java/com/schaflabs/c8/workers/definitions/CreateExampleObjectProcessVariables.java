package com.schaflabs.c8.workers.definitions;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CreateExampleObjectProcessVariables {
  @JsonProperty("name")
  private String name;

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
