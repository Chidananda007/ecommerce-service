package com.ecommerce.cart.service;

import com.wexl.cart.dto.CartDto;
import com.wexl.cart.entity.Cart;
import com.wexl.cart.entity.CartItem;
import com.wexl.cart.repository.CartItemRepository;
import com.wexl.cart.repository.CartRepository;
import com.wexl.product.dto.ProductDto;
import com.wexl.product.entity.Product;
import com.wexl.product.repository.ProductsRepository;
import com.wexl.retail.commons.errorcodes.InternalErrorCodes;
import com.wexl.retail.commons.exceptions.ApiException;
import com.wexl.retail.guardian.service.GuardianService;
import com.wexl.retail.model.User;
import com.wexl.retail.repository.UserRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

  private final CartRepository cartRepository;
  private final UserRepository userRepository;
  private final ProductsRepository productsRepository;
  private final CartItemRepository cartItemRepository;
  private final GuardianService guardianService;

  public List<CartDto.CartItemResponse> getAllCartProducts(String authUserId) {

    User user = guardianService.validateUser(authUserId);

    List<Cart> userCartItems = cartRepository.getAllByUserId(user.getId());
    if (userCartItems.isEmpty()) {
      return Collections.emptyList();
    }

    List<CartDto.CartItemResponse> cartItems = new ArrayList<>();
    for (Cart cart : userCartItems) {
      double cartTotalPrice =
          cart.getCartItems().stream()
              .mapToDouble(item -> item.getQuantity() * item.getUnitPrice())
              .sum();

      List<ProductDto.productResponse> products = buildProductResponseList(cart);

      cartItems.add(
          CartDto.CartItemResponse.builder()
              .cartId(cart.getId())
              .userId(cart.getUser().getId())
              .discountCode(cart.getDiscountCode())
              .status(cart.getStatus())
              .cartTotalPrice(cartTotalPrice)
              .product(products)
              .build());
    }

    return cartItems;
  }

  private List<ProductDto.productResponse> buildProductResponseList(Cart cart) {
    return cart.getCartItems().stream()
        .map(
            item ->
                ProductDto.productResponse
                    .builder()
                    .cartItemId(item.getId())
                    .id(item.getProduct().getId())
                    .title(item.getProduct().getTitle())
                    .description(item.getProduct().getDescription())
                    .thumbnail(item.getProduct().getThumbnail())
                    .price(item.getUnitPrice())
                    .status(item.getProduct().getStatus())
                    .type(item.getProduct().getType())
                    .quantity(item.getQuantity())
                    .build())
        .toList();
  }

  public void addItemToCart(CartDto.CartRequest request, Long cartId) {

    Cart cart =
        cartRepository
            .findById(cartId)
            .orElseThrow(
                () -> new ApiException(InternalErrorCodes.INVALID_REQUEST, "error.cartNotFound"));

    Product product =
        productsRepository
            .findById(request.productId())
            .orElseThrow(
                () ->
                    new ApiException(InternalErrorCodes.INVALID_REQUEST, "error.productNotFound"));

    var cartItems = cartItemRepository.findByCartAndProduct(cart, product);
    if (cartItems.isPresent()) {
      throw new ApiException(InternalErrorCodes.INVALID_REQUEST, "error.productAlreadyAdded");
    }

    CartItem cartItem = new CartItem();
    cartItem.setCart(cart);
    cartItem.setProduct(product);
    cartItem.setQuantity(request.quantity());
    cartItem.setUnitPrice(product.getPrice());

    cartItemRepository.save(cartItem);
  }

  public void deleteItemFromCart(long cartId, long cartItemId) {

    Cart cart =
        cartRepository
            .findById(cartId)
            .orElseThrow(
                () -> new ApiException(InternalErrorCodes.INVALID_REQUEST, "error.cartNotFound"));

    CartItem cartItem =
        cartItemRepository
            .findById(cartItemId)
            .orElseThrow(
                () ->
                    new ApiException(InternalErrorCodes.INVALID_REQUEST, "error.cartItemNotFound"));

    if (cartItem.getCart() == null || cartItem.getCart().getId() != cart.getId()) {
      throw new ApiException(InternalErrorCodes.INVALID_REQUEST, "error.invalid.cartItem");
    }

    cartItemRepository.delete(cartItem);
  }
}
