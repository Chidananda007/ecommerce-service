package com.ecommerce.service;

import com.ecommerce.entity.Products;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.requestdto.ProductDto;
import com.ecommerce.responsedto.ProductResponseDto;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductService {

  private final ProductRepository productRepository;

  public void createNewProduct(ProductDto.ProductCreateDto product) {

    Products productsSave = new Products();
    productsSave.setProductName(product.title());
    productsSave.setProductCategory(product.productCategory());
    productsSave.setProductPrice(product.productPrice());
    productRepository.save(productsSave);
  }

  public ResponseEntity<?> getProductById(Long user, Long productId) {
    Optional<Products> optionalProduct = productRepository.findByIdAndUser(productId, user);

    if (optionalProduct.isPresent()) {
      var product = optionalProduct.get();
      ProductResponseDto.ProductDetailsResponseDto productDetailsResponseDto =
          ProductResponseDto.ProductDetailsResponseDto.builder()
              .id(product.getId())
              .productName(product.getProductName())
              .productCategory(product.getProductCategory())
              .productPrice(product.getProductPrice())
              .build();
      return new ResponseEntity<>(productDetailsResponseDto, HttpStatus.OK);
    }
    return new ResponseEntity<>("Product not found", HttpStatus.NO_CONTENT);
  }

  public ResponseEntity<?> getProducts(Long userId) {
    return null;
  }

  public void updateProduct(Long userId) {}

  public void deleteProductByUser(Long userId) {}
}
