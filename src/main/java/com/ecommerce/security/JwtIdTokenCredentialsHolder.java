package com.ecommerce.security;

import static com.ecommerce.security.JwtConstants.*;
import static com.ecommerce.security.JwtConstants.FIRST_NAME;
import static com.ecommerce.security.JwtConstants.ID;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Data;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

@Data
public class JwtIdTokenCredentialsHolder {
  private String accessToken;
  private int userId;

  private long studentId;
  private String authUserId;
  private int classId;
  private String organization;
  private boolean premiumSubscription;
  private String userEmail;
  private List<String> userRoles;
  private String firstName;
  private String lastName;
  private List<String> subjects;
  private List<String> sections;

  public static <T> List<T> castList(Object obj, Class<T> clazz) {
    return (obj instanceof List<?> l)
        ? l.stream().map(clazz::cast).collect(Collectors.toList())
        : Collections.emptyList();
  }

  public JwtIdTokenCredentialsHolder() {
    var authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      var claims = ((JwtAuthentication) authentication).getJwtClaimsSet();
      this.authUserId = claims.getSubject();
      this.userId = (int) claims.get(ID);

      if (claims.get("studentId") != null) {
        this.studentId = (int) claims.get("studentId");
      }
      this.firstName = (String) claims.get(FIRST_NAME);
      this.lastName = (String) claims.get(LAST_NAME);
      userRoles = castList(claims.get(ROLES), String.class);
      this.subjects = castList(claims.get(SUBJECTS), String.class);
      this.sections = castList(claims.get(SECTIONS), String.class);

      if (claims.containsKey(CLASS_ID)) {
        this.classId = (int) claims.get(CLASS_ID);
      }
      if (claims.containsKey(EMAIL)) {
        this.userEmail = (String) claims.get(EMAIL);
      }
      if (claims.containsKey(IS_PREMIUM)) {
        this.premiumSubscription = (boolean) claims.get(IS_PREMIUM);
      } else {
        this.premiumSubscription = false;
      }
      this.organization = (String) claims.get(ORGANIZATION);
    }
  }

  public boolean hasRole(String[] acceptedRoles) {
    if (CollectionUtils.isEmpty(userRoles)) {
      return false;
    }
    if (ObjectUtils.isEmpty(acceptedRoles)) {
      return true;
    }

    Optional<String> possibleRole =
        userRoles.stream().filter(role -> exists(role, acceptedRoles)).findFirst();
    return possibleRole.isPresent();
  }

  private boolean exists(String roleToCheck, String[] acceptedRoles) {
    return Arrays.asList(acceptedRoles).contains(roleToCheck);
  }
}
