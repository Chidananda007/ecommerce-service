package com.ecommerce.order.controller;

import com.ecommerce.order.dto.OrderDto;
import com.ecommerce.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/orders")
public class OrderController {

  private final OrderService orderService;

  @PostMapping
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<?> createOrder(@RequestBody OrderDto.OrderRequestDto order) {
    return orderService.createOrder(order);
  }

  @GetMapping()
  @ResponseBody
  public ResponseEntity<?> getAllOrders(@RequestBody OrderDto.OrderRequestDto getOrders) {
    return orderService.getAllOrders(getOrders);
  }
}
