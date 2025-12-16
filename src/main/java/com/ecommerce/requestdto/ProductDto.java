package com.ecommerce.requestdto;

import lombok.NonNull;

public record ProductDto() {

  public record ProductCreateDto(
      @NonNull String title, @NonNull String productCategory, @NonNull Double productPrice) {}
}
