package com.amazon.repository;

import com.amazon.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Orders, Long> {


    List<Orders> findByUserId(Long id);
}
