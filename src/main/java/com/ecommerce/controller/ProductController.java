package com.ecommerce.controller;

import com.ecommerce.requestdto.ProductDto;
import com.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping(path = "/products")
public class ProductController {

  @Autowired ProductService productService;

  @PostMapping("/create/product")
  @ResponseBody
  @ResponseStatus(HttpStatus.CREATED)
  public void createNewProduct(@RequestBody ProductDto.ProductCreateDto product) {
    productService.createNewProduct(product);
  }

  @GetMapping("/get/product/{productId}")
  @ResponseBody
  public ResponseEntity<?> getProductById(@PathVariable("productId") Long productId) {
    return productService.getProductById(productId);
  }
}
