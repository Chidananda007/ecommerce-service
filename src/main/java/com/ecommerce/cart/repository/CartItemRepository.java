package com.ecommerce.cart.repository;

import com.wexl.cart.entity.Cart;
import com.wexl.cart.entity.CartItem;
import com.wexl.product.entity.Product;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface CartItemRepository extends JpaRepository<CartItem, Long> {

  Optional<CartItem> findByCartAndProduct(Cart cart, Product product);
}
