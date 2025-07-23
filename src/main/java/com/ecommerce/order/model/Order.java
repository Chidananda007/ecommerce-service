package com.ecommerce.order.model;

import com.ecommerce.generic.model.Discount;
import com.ecommerce.product.model.Product;
import com.ecommerce.user.model.User;
import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "orders")
public class Order {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column
  private Long id;

  @ManyToMany private List<Product> products;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private User user;

  @Column private long paymentId;

  @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private Discount discount;
}
