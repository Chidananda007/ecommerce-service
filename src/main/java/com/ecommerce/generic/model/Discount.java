package com.ecommerce.generic.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@RequiredArgsConstructor
@Table(name = "discounts")
public class Discount {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(unique = true, nullable = false)
  private String couponCode;

  private double discountPercentage;

  @Column(columnDefinition = "TEXT")
  private String description;

  private LocalDateTime expiryDate;
}
