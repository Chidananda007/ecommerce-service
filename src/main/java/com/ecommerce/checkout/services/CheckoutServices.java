package com.ecommerce.checkout.services;

import static com.wexl.betsignuplogin.services.BetServices.ORG_SLUG;
import static com.wexl.retail.util.Constants.WEXL_INTERNAL;

import com.razorpay.RazorpayClient;
import com.wexl.cart.entity.Cart;
import com.wexl.cart.repository.CartRepository;
import com.wexl.checkout.dto.CheckoutDto;
import com.wexl.discount.DiscountStatus;
import com.wexl.discount.repository.DiscountRepository;
import com.wexl.discount.services.DiscountServices;
import com.wexl.order.dto.OrderDto;
import com.wexl.order.entity.Order;
import com.wexl.order.entity.OrderItem;
import com.wexl.order.entity.OrderItemStatus;
import com.wexl.order.entity.OrderStatus;
import com.wexl.order.repository.OrderRepository;
import com.wexl.payment.PaymentRepository;
import com.wexl.payment.PaymentStatus;
import com.wexl.payment.entity.Payment;
import com.wexl.product.entity.ProductType;
import com.wexl.retail.auth.AuthService;
import com.wexl.retail.commons.errorcodes.InternalErrorCodes;
import com.wexl.retail.commons.exceptions.ApiException;
import com.wexl.retail.model.Student;
import com.wexl.retail.model.User;
import com.wexl.retail.student.attributes.dto.StudentAttributeDto;
import com.wexl.retail.student.attributes.model.StudentAttributeValueModel;
import com.wexl.retail.student.attributes.repository.StudentAttributeValueRepository;
import com.wexl.retail.student.attributes.service.StudentAttributeService;
import com.wexl.retail.telegram.service.UserService;
import com.wexl.retail.test.schedule.domain.ScheduleTest;
import com.wexl.retail.test.schedule.domain.ScheduleTestMetadata;
import com.wexl.retail.test.schedule.dto.SimpleScheduleTestRequest;
import com.wexl.retail.test.schedule.service.ScheduleTestService;
import com.wexl.retail.test.school.domain.TestDefinition;
import com.wexl.retail.test.school.domain.TestType;
import com.wexl.retail.test.school.repository.TestDefinitionRepository;
import com.wexl.retail.util.ValidationUtils;
import jakarta.transaction.Transactional;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CheckoutServices {

  private final AuthService authService;
  private final CartRepository cartRepository;
  private final OrderRepository orderRepository;
  private final UserService userService;
  private final PaymentRepository paymentRepository;
  private final DiscountServices discountServices;
  private final ScheduleTestService scheduleTestService;
  private final TestDefinitionRepository testDefinitionRepository;
  private final ValidationUtils validationUtils;
  private final DiscountRepository discountRepository;
  private final StudentAttributeService studentAttributeService;
  private final StudentAttributeValueRepository studentAttributeValueRepository;

  @Value("${app.razorPay.keyId}")
  private String rzPayKeyId;

  @Value("${app.razorPay.secretKey}")
  private String rzPaySecretKey;

  @Transactional
  public CheckoutDto.CheckoutResponse checkoutOrder(Long cartId) {
    Cart cart = validateCart(cartId);
    if (Objects.isNull(cart.getCartItems()) || cart.getCartItems().isEmpty()) {
      throw new ApiException(
          InternalErrorCodes.INVALID_REQUEST,
          "Your cart is empty,Please add product/products to continue");
    }
    Order order = createOrders(cart);
    return discountServices.buildCheckoutResponse(order);
  }

  private com.razorpay.Order createRzPayOrder(Order order) {
    try {
      var razorpay = new RazorpayClient(rzPayKeyId, rzPaySecretKey);
      var totalAmount =
          ((Objects.nonNull(order.getTotalAmountWithDiscount())
                  ? order.getTotalAmountWithDiscount()
                  : order.getTotalAmount())
              * 100);
      JSONObject orderRequest = new JSONObject();
      orderRequest.put("amount", totalAmount);
      orderRequest.put("currency", "INR");
      orderRequest.put("receipt", "txn_%s".formatted(order.getOrderNumber()));
      orderRequest.put("payment_capture", 1);
      JSONObject notes = new JSONObject();
      var user = order.getUser();
      notes.put("date", LocalDateTime.now().toString());
      notes.put("email", user.getEmail());
      notes.put("mobile_number", user.getMobileNumber());
      orderRequest.put("notes", notes);
      orderRequest.put("amount", totalAmount);
      return razorpay.orders.create(orderRequest);
    } catch (Exception e) {
      throw new ApiException(InternalErrorCodes.INVALID_REQUEST, "rzPayOrderError", e);
    }
  }

  private Order validateOrderByCart(Cart cart) {
    double totalAmount =
        cart.getCartItems().stream()
            .mapToDouble(item -> item.getUnitPrice() * item.getQuantity())
            .sum();
    var orders = orderRepository.findByUserAndStatus(cart.getUser(), OrderStatus.CREATED);
    if (!orders.isEmpty()) {
      var order = orders.stream().max(Comparator.comparing(Order::getCreatedAt)).orElseThrow();
      order.setTotalAmount(totalAmount);
      order.setTotalAmountWithDiscount(totalAmount);
      if (Objects.nonNull(order.getDiscountCode())) {
        var discount = discountServices.validateDiscount(order.getDiscountCode());
        var discountAmount = discountServices.evaluateDiscountAmount(discount, order);
        order.setDiscountAmount(discountAmount);
        order.setTotalAmountWithDiscount(order.getTotalAmount() - discountAmount);
      }
      return order;
    }
    long orderNumber = System.currentTimeMillis();
    String alphaNumericOrderNumber = Long.toString(orderNumber, 36).toUpperCase();
    return Order.builder()
        .orderNumber(alphaNumericOrderNumber)
        .totalAmount(totalAmount)
        .status(OrderStatus.CREATED)
        .totalAmountWithDiscount(totalAmount)
        .user(cart.getUser())
        .orderItems(Collections.emptyList())
        .build();
  }

  public Cart validateCart(Long cartId) {
    return cartRepository
        .findByIdAndUser(cartId, authService.getUserDetails())
        .orElseThrow(
            () -> new ApiException(InternalErrorCodes.INVALID_REQUEST, "error.InvalidUser"));
  }

  private Order createOrders(Cart cart) {
    Order order = validateOrderByCart(cart);
    var orderItemMap =
        order.getOrderItems().stream()
            .collect(Collectors.toMap(ot -> ot.getProduct().getId(), Function.identity()));
    List<OrderItem> orderItems =
        cart.getCartItems().stream()
            .map(
                cartItem -> {
                  var orderItem = orderItemMap.get(cartItem.getProduct().getId());
                  if (orderItem != null) {
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setUnitPrice(cartItem.getUnitPrice());
                    return orderItem;
                  }
                  return OrderItem.builder()
                      .order(order)
                      .product(cartItem.getProduct())
                      .unitPrice(cartItem.getUnitPrice())
                      .quantity(cartItem.getQuantity())
                      .status(OrderItemStatus.CREATED)
                      .build();
                })
            .toList();
    order.setOrderItems(new ArrayList<>(orderItems));
    return orderRepository.save(order);
  }

  @Transactional
  public CheckoutDto.PaymentResponse verifyPayment(
      Long orderId, CheckoutDto.VerifyPaymentRequest verifyPaymentRequest) {
    try {
      verifyPaymentSignature(verifyPaymentRequest);
      var user = authService.getUserDetails();
      var order = validateOrderByStatus(orderId, List.of(OrderStatus.INITIATED));
      var payment = createPayment(user, order, verifyPaymentRequest);
      var savedPayment = paymentRepository.save(payment);
      scheduleBetTestByOrder(order);
      incrementDiscountUsage(order);
      clearAllCartItems(user);
      return buildPaymentResponse(savedPayment);
    } catch (Exception e) {
      throw new ApiException(InternalErrorCodes.INVALID_REQUEST, e.getMessage(), e);
    }
  }

  private void scheduleBetTestByOrder(Order order) {
    var timestamp = Timestamp.valueOf(LocalDateTime.now());
    order.setStatus(OrderStatus.PAID);
    order.setUpdatedAt(timestamp);
    order
        .getOrderItems()
        .forEach(
            orderItem -> {
              if (ProductType.TEST.equals(orderItem.getProduct().getType())) {
                var scheduleTest = scheduleTest(order.getUser());
                orderItem.setItemDetails(
                    OrderDto.OrderItemDetails.builder()
                        .testScheduleIds(Set.of(scheduleTest.getId()))
                        .build());
                orderItem.setStatus(OrderItemStatus.PURCHASED);
                orderItem.setUpdatedAt(timestamp);
              }
            });
    orderRepository.save(order);
  }

  private void incrementDiscountUsage(Order order) {
    if (Objects.isNull(order.getDiscountCode()) && Objects.isNull(order.getDiscountAmount())) {
      return;
    }
    Optional.ofNullable(order.getDiscountCode())
        .ifPresent(
            code -> {
              var discount =
                  discountRepository.findByDiscountCodeAndStatus(code, DiscountStatus.ACTIVE);
              if (discount != null && discountServices.isValidDiscount(discount)) {
                discount.setCurrentUsage(
                    Optional.ofNullable(discount.getCurrentUsage()).orElse(0L) + 1);
                discountRepository.save(discount);
              }
            });
  }

  private void clearAllCartItems(User user) {
    var cart = cartRepository.findByUserAndDeletedAtIsNull(user);
    if (cart != null) {
      cart.getCartItems().clear();
      cart.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
      cartRepository.save(cart);
    }
  }

  private ScheduleTest scheduleTest(User user) {
    var testDefinition =
        testDefinitionRepository.getBetTests("BET-LE-%", TestType.MOCK_TEST.name(), WEXL_INTERNAL);
    var learnerExams =
        testDefinitionRepository.studentWrittenTest(
            "BET-LE-%", TestType.MOCK_TEST.name(), WEXL_INTERNAL, user.getId());
    var filterTest =
        testDefinition.stream()
            .filter(x -> !learnerExams.contains(x))
            .findFirst()
            .orElseThrow(
                () ->
                    new ApiException(
                        InternalErrorCodes.INVALID_REQUEST, "error.StudentTestNotFound"));
    var student = validationUtils.validateStudentByAuthId(user.getAuthUserId(), ORG_SLUG);

    var scheduleRequest =
        SimpleScheduleTestRequest.builder()
            .testDefinitionId(filterTest.getId())
            .allStudents(false)
            .message("All the Best")
            .startDate(Instant.now().toEpochMilli())
            .endDate(
                LocalDateTime.now()
                    .plusMonths(1)
                    .atZone(ZoneId.systemDefault())
                    .toInstant()
                    .toEpochMilli())
            .duration(120)
            .metadata(buildMetaData(student))
            .studentIds(Collections.singleton(student.getId()))
            .build();
    List<StudentAttributeValueModel> studentAttributes =
        studentAttributeValueRepository.findByStudentAndOrgSlug(
            user.getOrganization(), user.getStudentInfo().getId());

    var response = scheduleTestService.scheduleBetTest(filterTest, scheduleRequest, user);
    Map<String, String> attributes =
        Map.of("bet_corp_schedule_" + studentAttributes.size(), String.valueOf(response.getId()));
    var buildAttributes = StudentAttributeDto.Request.builder().attributes(attributes).build();
    studentAttributeService.saveStudentDefinitionAttributes(
        user.getAuthUserId(), user.getOrganization(), buildAttributes);
    return response;
  }

  private ScheduleTestMetadata buildMetaData(Student student) {
    var section = student.getSection();
    return ScheduleTestMetadata.builder()
        .board(section.getBoardSlug())
        .grade(section.getGradeSlug())
        .sections(Collections.singletonList(section.getName()))
        .build();
  }

  private CheckoutDto.PaymentResponse buildPaymentResponse(Payment savedPayment) {
    return CheckoutDto.PaymentResponse.builder()
        .paymentId(savedPayment.getId())
        .status(savedPayment.getPaymentStatus())
        .message("Payment verified successfully")
        .build();
  }

  private Payment createPayment(
      User user, Order order, CheckoutDto.VerifyPaymentRequest verifyPaymentRequest) {
    return Payment.builder()
        .user(user)
        .order(order)
        .paymentStatus(PaymentStatus.SUCCESS)
        .razorpayTxnId(verifyPaymentRequest.razorpayOrderId())
        .razorpayPaymentId(verifyPaymentRequest.razorpayPaymentId())
        .build();
  }

  private void verifyPaymentSignature(CheckoutDto.VerifyPaymentRequest request) {
    try {
      String generatedSignature =
          hmacSha256("%s|%s".formatted(request.razorpayOrderId(), request.razorpayPaymentId()));
      if (!generatedSignature.equals(request.razorpaySignature())) {
        throw new ApiException(
            InternalErrorCodes.INVALID_REQUEST, "Payment failed due to invalid signature");
      }
    } catch (Exception e) {
      throw new ApiException(InternalErrorCodes.INVALID_REQUEST, e.getMessage(), e);
    }
  }

  private String hmacSha256(String data) {
    try {
      Mac sha256Hmac = Mac.getInstance("HmacSHA256");
      SecretKeySpec secretKeySpec =
          new SecretKeySpec(rzPaySecretKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
      sha256Hmac.init(secretKeySpec);
      byte[] hash = sha256Hmac.doFinal(data.getBytes(StandardCharsets.UTF_8));
      return new String(Hex.encodeHex(hash));
    } catch (Exception e) {
      throw new ApiException(InternalErrorCodes.INVALID_REQUEST, e.getMessage(), e);
    }
  }

  public CheckoutDto.InitiatePaymentResponse initiatePaymentByOrder(Long orderId) {
    var order = validateOrderByStatus(orderId, List.of(OrderStatus.CREATED, OrderStatus.INITIATED));
    var tests =
        testDefinitionRepository.getUserTestDefForBet(
            "BET-LE-%", WEXL_INTERNAL, order.getUser().getId());
    var testPresent =
        testDefinitionRepository.getTestDefinitionByTestNameAndOrgSlug(
            "BET-LE-%",
            WEXL_INTERNAL,
            tests.isEmpty() ? null : tests.stream().map(TestDefinition::getId).toList());
    if (testPresent.isEmpty()) {
      throw new ApiException(InternalErrorCodes.NO_RECORD_FOUND, "error.NoTestDefinition");
    }
    final var rzPayOrder = createRzPayOrder(order);
    final String rzPayOrderId = rzPayOrder.get("id");
    order.setRzPayOrderId(rzPayOrderId);
    order.setStatus(OrderStatus.INITIATED);
    order.setUpdatedAt(Timestamp.valueOf(LocalDateTime.now()));
    final var updatedOrder = orderRepository.save(order);
    return CheckoutDto.InitiatePaymentResponse.builder()
        .orderId(updatedOrder.getId())
        .orderStatus(updatedOrder.getStatus())
        .orderNumber(updatedOrder.getOrderNumber())
        .rzPayOrderId(rzPayOrderId)
        .rzPayKey(rzPayKeyId)
        .message("Payment initiated successfully")
        .build();
  }

  private Order validateOrderByStatus(Long orderId, List<OrderStatus> status) {
    return orderRepository
        .findByIdAndStatusIn(orderId, status)
        .orElseThrow(
            () ->
                new ApiException(
                    InternalErrorCodes.INVALID_REQUEST,
                    "error.orderNotInitiated",
                    new String[] {status.getFirst().toString()}));
  }
}
