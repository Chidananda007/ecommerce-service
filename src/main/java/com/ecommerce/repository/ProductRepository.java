package com.ecommerce.repository;

import com.ecommerce.entity.Products;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Products, Long> {
  List<Products> findByProductCategory(String productCategory);

  Optional<Products> findByIdAndUser(Long productId, Long user);
}
