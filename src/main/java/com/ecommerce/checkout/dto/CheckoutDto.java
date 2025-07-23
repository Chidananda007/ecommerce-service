package com.ecommerce.checkout.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.wexl.order.entity.OrderStatus;
import com.wexl.payment.PaymentStatus;
import java.util.List;
import lombok.Builder;

public record CheckoutDto() {

  @Builder
  public record CheckoutResponse(
      @JsonProperty("order_id") Long orderId,
      @JsonProperty("user_name") String userName,
      @JsonProperty("mobile_number") String mobileNumber,
      String email,
      @JsonProperty("order_number") String orderNumber,
      @JsonProperty("discount_amount") Double discountAmount,
      String country,
      String state,
      @JsonProperty("total_amount_after_discount") Double totalAmountAfterDiscount,
      @JsonProperty("total_amount") double totalAmount,
      @JsonProperty("order_item_response") List<OrderItemResponse> orderItemResponses) {}

  @Builder
  public record OrderItemResponse(
      @JsonProperty("product_name") String productName,
      @JsonProperty("product_price") double productPrice,
      @JsonProperty("total_price") double totalPrice,
      @JsonProperty("product_quantity") double productQuantity) {}

  @Builder
  public record VerifyPaymentRequest(
      @JsonProperty("razorpay_payment_id") String razorpayPaymentId,
      @JsonProperty("razorpay_order_id") String razorpayOrderId,
      @JsonProperty("razorpay_signature") String razorpaySignature) {}

  @Builder
  public record InitiatePaymentResponse(
      @JsonProperty("order_id") Long orderId,
      @JsonProperty("order_status") OrderStatus orderStatus,
      @JsonProperty("order_number") String orderNumber,
      @JsonProperty("rzPay_order_id") String rzPayOrderId,
      @JsonProperty("rzPay_key") String rzPayKey,
      String message) {}

  @Builder
  public record PaymentResponse(
      @JsonProperty("payment_id") Long paymentId,
      @JsonProperty("payment_status") PaymentStatus status,
      String message) {}
}
