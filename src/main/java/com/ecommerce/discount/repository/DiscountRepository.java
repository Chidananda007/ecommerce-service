package com.ecommerce.discount.repository;

import com.wexl.discount.DiscountStatus;
import com.wexl.discount.entity.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;

@Repository
@EnableJpaRepositories
public interface DiscountRepository extends JpaRepository<Discount, Long> {

  Discount findByDiscountCodeAndStatus(String discountCode, DiscountStatus status);
}
