package com.ecommerce.responsedto;

import lombok.Builder;

public record UserResponseDto() {
  @Builder
  public record UserDetailsResponseDto(
      Long id,
      String accessToken,
      String userFirstName,
      String userLastName,
      String userName,
      Long mobileNumber) {}
}
