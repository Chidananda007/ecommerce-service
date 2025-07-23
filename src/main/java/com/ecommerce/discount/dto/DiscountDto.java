package com.ecommerce.discount.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

public record DiscountDto() {

  @Builder
  public record DiscountRequest(
      @JsonProperty("order_number") String orderNumber,
      @JsonProperty("discount_code") String discountCode) {}
}
