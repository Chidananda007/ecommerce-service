package com.amazon.controller;

import com.amazon.requestdto.OrderDto;
import com.amazon.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/orders")
public class OrderController {

    @Autowired
    OrderService orderService;

    @PostMapping("/create/order")
    @ResponseBody
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<?> createOrder(@RequestBody OrderDto.OrderRequestDto order){
       return orderService.createOrder(order);
    }


    @GetMapping("/get/all/orders")
    @ResponseBody
    public ResponseEntity<?> getAllOrders(@RequestBody OrderDto.OrderRequestDto getOrders){
       return  orderService.getAllOrders(getOrders);
    }


}
