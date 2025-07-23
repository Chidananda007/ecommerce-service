package com.ecommerce.payment.entity;

import com.wexl.order.entity.Order;
import com.wexl.payment.PaymentStatus;
import com.wexl.retail.model.Model;
import com.wexl.retail.model.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "payments")
public class Payment extends Model {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "order_id")
  private Order order;

  @Column(name = "transaction_id")
  private String razorpayTxnId;

  private String razorpayPaymentId;

  @Enumerated(EnumType.STRING)
  private PaymentStatus paymentStatus;
}
