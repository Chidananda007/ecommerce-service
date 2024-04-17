package com.amazon.service;

import com.amazon.entity.Orders;
import com.amazon.entity.Products;
import com.amazon.entity.Users;
import com.amazon.repository.OrderRepository;
import com.amazon.repository.ProductRepository;
import com.amazon.repository.UserRepositroy;
import com.amazon.requestdto.OrderDto;
import com.amazon.responsedto.OrderResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;


import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    UserRepositroy userRepositroy;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    RestTemplate restTemplate;

    private static final int DEFAULT_ETA = 3;

    public ResponseEntity<?> createOrder(OrderDto.OrderRequestDto order) {
        Optional<Users> users = userRepositroy.findByUserNameAndPassword(order.userName(), order.password());
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

    public void selectProductsAndCreateOrder(OrderDto.OrderRequestDto order, Users users) {
        List<Products> products = productRepository.findByProductCategory(order.psd().productCategory());
        if (products.isEmpty()) {
            throw new RuntimeException("Products not found");
        }

        String url = "http://localhost:8090/payments/amzpyt";
        HttpEntity<List<Products>> app = new HttpEntity<>(products);
        ResponseEntity<Long> responseEntity = restTemplate.exchange(url, HttpMethod.POST, app, Long.class);
        Long longPaymentId = responseEntity.getBody();
        long convertedPaymentId = longPaymentId != null ? longPaymentId : 0;

        Orders orders = new Orders();
        orders.setEta(DEFAULT_ETA);
        orders.setProducts(products);
        orders.setUserId(users.getId());
        orders.setPaymentId(convertedPaymentId);
        orderRepository.save(orders);

    }

    public ResponseEntity<?> getAllOrders(OrderDto.OrderRequestDto getOrders) {
        Optional<Users> users = userRepositroy.findByUserNameAndPassword(getOrders.userName(), getOrders.password());
        if (users.isEmpty()) {
            return new ResponseEntity<>("User Not Found", HttpStatus.NOT_FOUND);
        }

        List<Orders> orders = orderRepository.findByUserId(users.get().getId());
        if (orders.isEmpty()) {
            return new ResponseEntity<>("User is not having any Orders", HttpStatus.NOT_FOUND);
        }

        List<OrderResponseDto.OrderDetailsResponseDto> response = new ArrayList<>();
        for (Orders order : orders) {
            OrderResponseDto.OrderDetailsResponseDto o = OrderResponseDto.OrderDetailsResponseDto.builder()
                    .id(order.getId())
                    .eta(order.getEta())
                    .build();
            response.add(o);
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
