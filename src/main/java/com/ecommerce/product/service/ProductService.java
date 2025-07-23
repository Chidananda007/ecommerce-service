package com.ecommerce.product.service;

import com.ecommerce.product.dto.ProductDto;
import com.ecommerce.product.dto.ProductResponseDto;
import com.ecommerce.product.model.Product;
import com.ecommerce.product.repository.ProductRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

  @Autowired ProductRepository productRepository;

  public void createNewProduct(ProductDto.ProductCreateDto product) {

    Product productSave = new Product();
    productSave.setProductName(product.productName());
    productSave.setProductCategory(product.productCategory());
    productSave.setProductPrice(product.productPrice());
    productRepository.save(productSave);
  }

  public ResponseEntity<?> getProductById(Long productId) {
    Optional<Product> p = productRepository.findById(productId);

    if (p.isPresent()) {
      ProductResponseDto.ProductDetailsResponseDto productDetailsResponseDto =
          ProductResponseDto.ProductDetailsResponseDto.builder()
              .id(p.get().getId())
              .productName(p.get().getProductName())
              .productCategory(p.get().getProductCategory())
              .productPrice(p.get().getProductPrice())
              .build();

      return new ResponseEntity<>(productDetailsResponseDto, HttpStatus.OK);
    } else {
      return new ResponseEntity<>("Product not found", HttpStatus.NO_CONTENT);
    }
  }
}
