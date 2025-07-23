package com.ecommerce.order.dto;

import lombok.Builder;

public record OrderResponseDto() {

  @Builder
  public record OrderDetailsResponseDto(Long id, int eta) {}
}
