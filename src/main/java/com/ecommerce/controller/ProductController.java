package com.ecommerce.controller;

import com.ecommerce.requestdto.ProductDto;
import com.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/users/{userId}/products")
public class ProductController {

  private final ProductService productService;

  @PostMapping
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  public void createNewProduct(@RequestBody ProductDto.ProductCreateDto product) {
    productService.createNewProduct(product);
  }

  @ResponseBody
  @PutMapping()
  public void updateProduct(@PathVariable("userId") Long userId) {
    productService.updateProduct(userId);
  }

  @ResponseBody
  @GetMapping
  public ResponseEntity<?> getProductsByUser(@PathVariable("userId") Long userId) {
    return productService.getProducts(userId);
  }

  @ResponseBody
  @GetMapping("/{productId}")
  public ResponseEntity<?> getProductById(@PathVariable Long userId, @PathVariable Long productId) {
    return productService.getProductById(userId, productId);
  }

  @ResponseBody
  @DeleteMapping()
  public void deleteProduct(@PathVariable Long userId) {
    productService.deleteProductByUser(userId);
  }
}
