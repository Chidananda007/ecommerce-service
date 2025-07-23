package com.ecommerce.cart.controller;

import com.wexl.cart.dto.CartDto;
import com.wexl.cart.service.CartService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/bet-exams")
@RequiredArgsConstructor
public class CartController {

  private final CartService cartService;

  @GetMapping("/students/{authUserId}/carts")
  public List<CartDto.CartItemResponse> getAllCartProducts(@PathVariable String authUserId) {

    return cartService.getAllCartProducts(authUserId);
  }

  @PostMapping("/carts/{id}/cart-items")
  private ResponseEntity<String> addProductToCart(
      @RequestBody CartDto.CartRequest cartRequest, @PathVariable Long id) {

    cartService.addItemToCart(cartRequest, id);
    return ResponseEntity.ok("Item added to cart successfully");
  }

  @DeleteMapping("/carts/{id}/cart-items/{cartItemId}")
  public ResponseEntity<String> deleteItemFromCart(
      @PathVariable("id") long cartId, @PathVariable("cartItemId") long cartItemId) {

    cartService.deleteItemFromCart(cartId, cartItemId);
    return ResponseEntity.ok("Item removed from cart successfully");
  }
}
