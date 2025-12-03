package com.ecommerce.entity;

import com.ecommerce.utils.Model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@RequiredArgsConstructor
@Table(name = "users")
public class User extends Model {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "user_first_name")
  private String userFirstName;

  @Column(name = "user_last_name")
  private String userLastName;

  @Column(name = "auth_user_id", unique = true, nullable = false)
  private String authUserId;

  @Column(name = "email", unique = true, nullable = false)
  private String email;

  @Column(name = "username", unique = true, nullable = false)
  private String userName;

  @Column(name = "password")
  private String password;

  @Column(name = "mobile_number")
  private Long mobileNumber;

  @Enumerated(EnumType.STRING)
  private RoleTemplate roleTemplate;
}
