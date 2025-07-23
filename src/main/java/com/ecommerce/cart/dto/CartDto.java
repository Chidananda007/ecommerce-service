package com.ecommerce.cart.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wexl.product.dto.ProductDto;
import java.util.List;
import lombok.Builder;

public record CartDto() {

  @Builder
  public record CartItemResponse(
      @JsonProperty("cart_id") Long cartId,
      @JsonProperty("user_id") Long userId,
      @JsonProperty("discount_code") String discountCode,
      @JsonProperty("status") String status,
      @JsonProperty("cart_total_price") Double cartTotalPrice,
      List<ProductDto.productResponse> product) {}

  @Builder
  public record CartRequest(
      @JsonProperty("auth_user_id") String authUserId,
      @JsonProperty("product_id") Long productId,
      @JsonProperty("quantity") Long quantity) {}
}
