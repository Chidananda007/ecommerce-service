package com.ecommerce.repository;

import com.ecommerce.entity.Orders;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {

  List<Orders> findByUserId(Long id);
}
