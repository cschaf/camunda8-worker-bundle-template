package com.schaflabs.c8.workers.configurations;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "workers.common")
@PropertySource(value = {"classpath:application.yaml"})
@Data
public class CommonProperties {
  private String scheme;
  private String host;
}
