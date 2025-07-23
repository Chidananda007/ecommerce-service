package com.ecommerce.discount.services;

import com.wexl.checkout.dto.CheckoutDto;
import com.wexl.discount.DiscountStatus;
import com.wexl.discount.dto.DiscountDto;
import com.wexl.discount.entity.Discount;
import com.wexl.discount.repository.DiscountRepository;
import com.wexl.order.entity.Order;
import com.wexl.order.repository.OrderRepository;
import com.wexl.retail.commons.errorcodes.InternalErrorCodes;
import com.wexl.retail.commons.exceptions.ApiException;
import com.wexl.retail.telegram.service.UserService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DiscountServices {

  private final DiscountRepository discountRepository;
  private final OrderRepository orderRepository;
  private final UserService userService;

  public CheckoutDto.CheckoutResponse applyDiscount(DiscountDto.DiscountRequest request) {
    var optionalOrder = orderRepository.findByOrderNumber(request.orderNumber());
    if (optionalOrder.isEmpty()) {
      throw new ApiException(InternalErrorCodes.INVALID_REQUEST, "error.OrderNotFound");
    }
    var discount = validateDiscount(request.discountCode());
    var order = optionalOrder.get();
    order.setDiscountCode(discount.getDiscountCode());
    var discountAmount = evaluateDiscountAmount(discount, order);
    order.setDiscountAmount(discountAmount);
    order.setTotalAmountWithDiscount(order.getTotalAmount() - discountAmount);
    return buildCheckoutResponse(orderRepository.save(order));
  }

  public Discount validateDiscount(String discountCode) {
    var discount =
        discountRepository.findByDiscountCodeAndStatus(discountCode, DiscountStatus.ACTIVE);
    if (Objects.nonNull(discount) && isValidDiscount(discount)) {
      return discount;
    }
    throw new ApiException(InternalErrorCodes.INVALID_REQUEST, "error.invalidDiscountCode");
  }

  public boolean isValidDiscount(Discount discount) {
    var today = LocalDateTime.now();
    var currentUsage = Optional.ofNullable(discount.getCurrentUsage()).orElse(0L);
    return (today.isAfter(discount.getStartDate()) && today.isBefore(discount.getEndTime()))
        || discount.getUsageLimit() > currentUsage;
  }

  public CheckoutDto.CheckoutResponse buildCheckoutResponse(Order order) {
    List<CheckoutDto.OrderItemResponse> orderItemResponse =
        order.getOrderItems().stream()
            .map(
                item ->
                    CheckoutDto.OrderItemResponse.builder()
                        .productName(item.getProduct().getTitle())
                        .productQuantity(item.getQuantity())
                        .totalPrice(item.getUnitPrice() * item.getQuantity())
                        .productPrice(item.getUnitPrice())
                        .build())
            .toList();
    var addresses = order.getUser().getAddresses();
    return CheckoutDto.CheckoutResponse.builder()
        .totalAmount(order.getTotalAmount())
        .discountAmount(order.getDiscountAmount())
        .totalAmountAfterDiscount(order.getTotalAmountWithDiscount())
        .orderId(order.getId())
        .userName(userService.getNameByUserInfo(order.getUser()))
        .mobileNumber(order.getUser().getMobileNumber())
        .email(order.getUser().getEmail())
        .orderNumber(order.getOrderNumber())
        .orderItemResponses(orderItemResponse)
        .country(Objects.nonNull(addresses) ? addresses.getCountry() : null)
        .state(Objects.nonNull(addresses) ? addresses.getState() : null)
        .build();
  }

  public double evaluateDiscountAmount(Discount discount, Order order) {
    var totalAmount = order.getTotalAmount();
    double discountPercentage = Optional.ofNullable(discount.getDiscountPercentage()).orElse(0.0);
    if (discountPercentage < 0 || discountPercentage > 100) {
      throw new ApiException(InternalErrorCodes.INVALID_REQUEST, "Could not apply discount");
    }
    return Math.round((totalAmount * discountPercentage) / 100.0);
  }
}
