package com.amazon.service;

import com.amazon.entity.Products;
import com.amazon.entity.Users;
import com.amazon.repository.ProductRepository;
import com.amazon.requestdto.ProductDto;
import com.amazon.responsedto.ProductResponseDto;
import com.amazon.responsedto.UserResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    @Autowired
    ProductRepository productRepository;

    public void createNewProduct(ProductDto.ProductCreateDto product) {

      Products productsSave = new Products();
      productsSave.setProductName(product.productName());
      productsSave.setProductCategory(product.productCategory());
      productsSave.setProductPrice(product.productPrice());
      productRepository.save(productsSave);
    }

    public ResponseEntity<?> getProductById(Long productId) {
        Optional<Products> p = productRepository.findById(productId);


            if (p.isPresent()) {
                ProductResponseDto.ProductDetailsResponseDto productDetailsResponseDto = ProductResponseDto.ProductDetailsResponseDto.builder()
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

