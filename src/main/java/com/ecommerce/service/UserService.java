package com.ecommerce.service;

import com.ecommerce.common.EmailDto;
import com.ecommerce.common.EmailService;
import com.ecommerce.entity.RoleTemplate;
import com.ecommerce.entity.User;
import com.ecommerce.repository.UserRepositroy;
import com.ecommerce.requestdto.UserDto;
import com.ecommerce.responsedto.UserResponseDto;
import com.ecommerce.security.JwtConstants;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import javax.crypto.spec.SecretKeySpec;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

  @Value("${jwt.tokenSecret}")
  private String tokenSecret;

  @Value("${jwt.validityInDays}")
  private Long tokenExpiration;

  private final UserRepositroy userRepository;

  private final PasswordEncoder passwordEncoder;

  private final EmailService emailService;

  public void createNewUser(UserDto.UserSignUpRequest request) {
    log.info("Creating new user with username: {}", request.userName());

    var userByUserName = userRepository.findByUserName(request.userName());
    var userByEmail = userRepository.findByEmail(request.userName());
    if (userByUserName.isPresent() || userByEmail.isPresent()) {
      log.error("User with given username or email already exists: {}", request.userName());
      throw new RuntimeException("User with given username or email already exists");
    }
    try {
      var savedUser = userRepository.save(buildUser(request, new User()));
      var response = emailService.sendEmail(buildEmailRequest(savedUser));
      if (Objects.nonNull(response) && "success".equalsIgnoreCase(response.status())) {
        savedUser.setIsEmailSent(Boolean.TRUE);
        userRepository.save(savedUser);
      }
    } catch (Exception e) {
      throw new RuntimeException("Something went wrong : %s ".formatted(e.getMessage()), e);
    }
  }

  private EmailDto.EmailRequest buildEmailRequest(User user) {
    String subject = "You are successfully registered!";
    String body =
        "Dear %s,\n\nThank you for signing up on our e-commerce platform. We're excited to have you on board!\n\nBest regards,\nE-Commerce Team"
            .formatted(user.getUserFirstName());
    return EmailDto.EmailRequest.builder()
        .userEmail(user.getEmail())
        .receiver(user.getEmail())
        .name(String.format("%s %s", user.getUserFirstName(), user.getUserLastName()))
        .subject(subject)
        .mobile(Optional.ofNullable(user.getMobileNumber()).map(String::valueOf).orElse(null))
        .message(body)
        .build();
  }

  public User buildUser(UserDto.UserSignUpRequest request, User user) {
    user.setUserFirstName(request.firstName());
    user.setUserLastName(request.lastName());
    user.setUserName(request.userName());
    user.setPassword(passwordEncoder.encode(request.password()));
    user.setEmail(request.email());
    user.setAuthUserId(UUID.randomUUID().toString());
    user.setMobileNumber(request.mobileNumber());
    user.setRoleTemplate(RoleTemplate.CUSTOMER);
    return user;
  }

  public ResponseEntity<?> getUser(UserDto.UserLoginRequest request) {
    Optional<User> optionalUser = userRepository.findByUserName(request.userName());

    try {
      return optionalUser
          .map(
              user -> {
                if (!passwordEncoder.matches(request.password(), user.getPassword())) {
                  throw new RuntimeException("Invalid password");
                }
                UserResponseDto.UserDetailsResponseDto userDetailsResponseDto =
                    UserResponseDto.UserDetailsResponseDto.builder()
                        .id(user.getId())
                        .accessToken(createJwtToken(user.getUserName()))
                        .build();
                return new ResponseEntity<>(userDetailsResponseDto, HttpStatus.OK);
              })
          .orElseThrow(() -> new RuntimeException("User not found"));
    } catch (Exception e) {
      throw new RuntimeException("Failed to fetch user : %s ".formatted(e.getMessage()), e);
    }
  }

  public String createJwtToken(String username) {
    final User user =
        userRepository
            .findByUserName(username)
            .orElseThrow(() -> new RuntimeException("Invalid username or password"));
    return getIdTokenForUser(user);
  }

  private String getIdTokenForUser(User user) {
    var tokenSignInKey =
        new SecretKeySpec(
            Base64.getDecoder().decode(tokenSecret), SignatureAlgorithm.HS256.getJcaName());
    var currentDate = Instant.now();
    return Jwts.builder()
        .setSubject(user.getAuthUserId())
        .setId(String.valueOf(user.getId()))
        .setIssuedAt(Date.from(currentDate))
        .setExpiration(Date.from(currentDate.plus(tokenExpiration, ChronoUnit.DAYS)))
        .claim(JwtConstants.SUB, user.getAuthUserId())
        .claim(JwtConstants.MOBILE_NUMBER, user.getMobileNumber())
        .claim(JwtConstants.FIRST_NAME, user.getUserFirstName())
        .claim(JwtConstants.LAST_NAME, user.getUserLastName())
        .claim(JwtConstants.ROLES, Collections.singletonList(user.getRoleTemplate()))
        .claim(JwtConstants.EMAIL, user.getEmail())
        .signWith(tokenSignInKey, SignatureAlgorithm.HS256)
        .compact();
  }

  public ResponseEntity<List<UserResponseDto.UserDetailsResponseDto>> getAllUsers() {
    List<User> allUsers = userRepository.findAll();
    List<UserResponseDto.UserDetailsResponseDto> userDetailsList = new ArrayList<>();

    for (User user : allUsers) {
      UserResponseDto.UserDetailsResponseDto userDetailsResponseDto =
          UserResponseDto.UserDetailsResponseDto.builder()
              .id(user.getId())
              .userFirstName(user.getUserFirstName())
              .userLastName(user.getUserLastName())
              .userName(user.getUserName())
              .mobileNumber(user.getMobileNumber())
              .accessToken(createJwtToken(user.getUserName()))
              .build();
      userDetailsList.add(userDetailsResponseDto);
    }

    if (!userDetailsList.isEmpty()) {
      return new ResponseEntity<>(userDetailsList, HttpStatus.OK);
    } else {
      return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
  }
}
