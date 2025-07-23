package com.ecommerce.order.service;

import com.ecommerce.order.dto.OrderDto;
import com.ecommerce.order.dto.OrderResponseDto;
import com.ecommerce.order.model.Order;
import com.ecommerce.order.repository.OrderRepository;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.user.model.User;
import com.ecommerce.user.repository.UserRepositroy;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OrderService {

  private final OrderRepository orderRepository;

  private final UserRepositroy userRepositroy;

  private final ProductRepository productRepository;

  private final RestTemplate restTemplate;

  public ResponseEntity<?> createOrder(OrderDto.OrderRequestDto order) {
    Optional<User> users =
        userRepositroy.findByUserNameAndPassword(order.userName(), order.password());
    if (users.isEmpty()) {
      return new ResponseEntity<>("User Not Found", HttpStatus.NOT_FOUND);
    }

    try {
      selectProductsAndCreateOrder(order, users.get());
      return new ResponseEntity<>("Order created successfully", HttpStatus.CREATED);
    } catch (RuntimeException e) {
      return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
  }

  public void selectProductsAndCreateOrder(OrderDto.OrderRequestDto order, User user) {
    List<Product> products = productRepository.findByProductCategory(order.psd().productCategory());
    if (products.isEmpty()) {
      throw new RuntimeException("Products not found");
    }

    var paymentRef = proceedPayment(products);

    Order orders = new Order();
    orders.setProducts(products);
    orders.setUser(user);
    orders.setPaymentId(paymentRef);
    orderRepository.save(orders);
  }

  private long proceedPayment(List<Product> products) {
    String url = "http://localhost:8090/payments/amzpyt";
    HttpEntity<List<Product>> app = new HttpEntity<>(products);
    ResponseEntity<Long> responseEntity =
        restTemplate.exchange(url, HttpMethod.POST, app, Long.class);
    Long longPaymentId = responseEntity.getBody();
    return longPaymentId != null ? longPaymentId : 0;
  }

  public ResponseEntity<?> getAllOrders(OrderDto.OrderRequestDto getOrders) {
    Optional<User> users =
        userRepositroy.findByUserNameAndPassword(getOrders.userName(), getOrders.password());
    if (users.isEmpty()) {
      return new ResponseEntity<>("User Not Found", HttpStatus.NOT_FOUND);
    }

    List<Order> orders = orderRepository.findByUserId(users.get().getId());
    if (orders.isEmpty()) {
      return new ResponseEntity<>("User is not having any Orders", HttpStatus.NOT_FOUND);
    }

    List<OrderResponseDto.OrderDetailsResponseDto> response = new ArrayList<>();
    for (Order order : orders) {
      OrderResponseDto.OrderDetailsResponseDto o =
          OrderResponseDto.OrderDetailsResponseDto.builder().id(order.getId()).build();
      response.add(o);
    }

    return new ResponseEntity<>(response, HttpStatus.OK);
  }
}
