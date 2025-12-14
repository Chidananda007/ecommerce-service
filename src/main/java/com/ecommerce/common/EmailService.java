package com.ecommerce.common;

import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

  @Value("${app.email.url}")
  private String emailBaseUrl;

  private final RestTemplate restTemplate;

  public EmailDto.EmailResponse sendEmail(EmailDto.EmailRequest emailRequest) {
    try {
      String emailServiceUrl = String.format("%s/users/contact-us", emailBaseUrl);
      var response =
          restTemplate.postForEntity(emailServiceUrl, emailRequest, EmailDto.EmailResponse.class);
      if (Objects.nonNull(response.getBody()) && response.getStatusCode().is2xxSuccessful()) {
        return response.getBody();
      }
      return null;
    } catch (Exception e) {
      log.error("Failed to send email to {}: {}", emailRequest.receiver(), e.getMessage(), e);
      return null;
    }
  }
}
