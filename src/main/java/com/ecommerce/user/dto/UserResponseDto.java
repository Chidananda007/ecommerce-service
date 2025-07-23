package com.ecommerce.user.dto;

import lombok.Builder;

public record UserResponseDto() {
  @Builder
  public record UserDetailsResponseDto(
      Long id, String userFirstName, String userLastName, String userName, Long mobileNumber) {}
}
