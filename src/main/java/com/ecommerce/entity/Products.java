package com.ecommerce.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Table
@Entity
@Setter
@Getter
@RequiredArgsConstructor
public class Products {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column private String productName;

  @Column private String productCategory;

  @Column private Double productPrice;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private User user;
}
