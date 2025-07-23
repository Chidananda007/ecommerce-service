package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ProductDto;
import com.ecommerce.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping(path = "/products")
public class ProductController {

  private final ProductService productService;

  @PostMapping()
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  public void createNewProduct(@RequestBody ProductDto.ProductCreateDto product) {
    productService.createNewProduct(product);
  }

  @GetMapping("/{productId}")
  @ResponseBody
  public ResponseEntity<?> getProductById(@PathVariable("productId") Long productId) {
    return productService.getProductById(productId);
  }
}
