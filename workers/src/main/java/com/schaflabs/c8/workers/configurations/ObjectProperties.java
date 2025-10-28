package com.schaflabs.c8.workers.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "workers.object")
@PropertySource(value = {"classpath:application.yaml"})
@Data
public class ObjectProperties {
  private String getAllObjectRoute;
  private String getObjectByIdRoute;
  private String createObjectRoute;
  private String updateObjectRoute;
  private String deleteObjectRoute;
}
