package com.ecommerce.product.dto;

import lombok.NonNull;

public record ProductDto() {

  public record ProductCreateDto(
      @NonNull String productName, @NonNull String productCategory, @NonNull Double productPrice) {}
}
