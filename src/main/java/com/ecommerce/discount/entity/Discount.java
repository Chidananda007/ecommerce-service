package com.ecommerce.discount.entity;

import com.wexl.discount.DiscountStatus;
import com.wexl.retail.model.Model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "discounts")
public class Discount extends Model {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "discount_code", unique = true)
  private String discountCode;

  @Column(name = "description")
  private String description;

  @Column(name = "thumbnail")
  private String thumbnail;

  @Column(name = "price")
  private Double price;

  @Max(100)
  @Column(name = "discount_percentage")
  private Double discountPercentage;

  private LocalDateTime startDate;

  private LocalDateTime endTime;

  @Column(name = "usage_limit", nullable = false)
  private long usageLimit = 0;

  private Long currentUsage;

  @Enumerated(EnumType.STRING)
  private DiscountStatus status;

  @Column(name = "type")
  private String type;
}
