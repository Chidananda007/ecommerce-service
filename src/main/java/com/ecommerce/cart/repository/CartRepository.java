package com.ecommerce.cart.repository;

import com.wexl.cart.entity.Cart;
import com.wexl.retail.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface CartRepository extends JpaRepository<Cart, Long> {

  List<Cart> getAllByUserId(Long userId);

  Cart findByUserAndDeletedAtIsNull(User user);

  Optional<Cart> findByIdAndUser(Long cartId, User user);
}
