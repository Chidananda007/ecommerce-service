package com.ecommerce.controller;

import com.ecommerce.requestdto.OrderDto;
import com.ecommerce.service.OrderService;
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

  @GetMapping("/get/all/orders")
  @ResponseBody
  public ResponseEntity<?> getAllOrders(@RequestBody OrderDto.OrderRequestDto getOrders) {
    return orderService.getAllOrders(getOrders);
  }
}
