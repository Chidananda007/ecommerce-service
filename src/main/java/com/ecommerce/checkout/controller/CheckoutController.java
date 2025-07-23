package com.ecommerce.checkout.controller;

import com.wexl.checkout.dto.CheckoutDto;
import com.wexl.checkout.services.CheckoutServices;
import com.wexl.retail.commons.security.annotation.IsStudent;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/bet-exams")
public class CheckoutController {

  private final CheckoutServices checkoutServices;

  @IsStudent
  @PostMapping("/carts/{cartId}/checkout")
  public CheckoutDto.CheckoutResponse checkoutOrder(@PathVariable Long cartId) {
    return checkoutServices.checkoutOrder(cartId);
  }

  @IsStudent
  @PostMapping("/orders/{orderId}/payments:initiate")
  public CheckoutDto.InitiatePaymentResponse initiatePayment(
      @PathVariable("orderId") Long orderId) {
    return checkoutServices.initiatePaymentByOrder(orderId);
  }

  @IsStudent
  @PostMapping("/orders/{orderId}/payments:verify")
  public CheckoutDto.PaymentResponse verifyPayment(
      @PathVariable("orderId") Long orderId,
      @Valid @RequestBody CheckoutDto.VerifyPaymentRequest verifyPaymentRequest) {
    return checkoutServices.verifyPayment(orderId, verifyPaymentRequest);
  }
}
