package com.ecommerce.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class StartupConfig {

  @Value("${server.port:8080}")
  private String serverPort;

  @Value("${spring.h2.console.path:/h2-console}")
  private String h2ConsolePath;

  @Bean
  public CommandLineRunner printH2ConsoleUrl() {
    return args -> {
      String h2ConsoleUrl = "http://localhost:" + serverPort + h2ConsolePath;
      log.info(
          "\n\n"
              + "==========================================\n"
              + "H2 Console is available at: "
              + h2ConsoleUrl
              + "\n"
              + "==========================================\n");
    };
  }
}
