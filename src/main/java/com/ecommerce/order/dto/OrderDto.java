package com.ecommerce.order.dto;

import lombok.NonNull;

public record OrderDto() {

  public record OrderRequestDto(
      @NonNull String userName, @NonNull String password, ProductSearchDto psd) {}

  public record ProductSearchDto(@NonNull String productCategory) {}
}
