package com.ecommerce.discount.controller;

import com.wexl.checkout.dto.CheckoutDto;
import com.wexl.discount.dto.DiscountDto;
import com.wexl.discount.services.DiscountServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
public class DiscountController {

  private final DiscountServices discountServices;

  @PostMapping("/bet-exams/carts/{cartId}/discount")
  public CheckoutDto.CheckoutResponse applyDiscount(
      @Valid @RequestBody DiscountDto.DiscountRequest request) {
    return discountServices.applyDiscount(request);
  }
}
