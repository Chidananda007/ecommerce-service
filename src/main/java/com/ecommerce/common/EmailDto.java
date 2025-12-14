package com.ecommerce.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.NonNull;

public record EmailDto() {

  @Builder
  public record EmailRequest(
      @NonNull String name,
      @NonNull String receiver,
      @JsonProperty("user_email") String userEmail,
      String subject,
      String message,
      String mobile) {}

  @Builder
  public record EmailResponse(
      String receiver, String status, String message, String providerResponse) {}
}
