package com.ecommerce.security;

import static com.ecommerce.security.JwtConstants.*;
import static java.util.List.of;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.security.Key;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.crypto.spec.SecretKeySpec;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

@Component
public class AccessTokenProcessor {

  private final String tokenSecret;
  private final String[] disabledEmails;

  public AccessTokenProcessor(
      @Value("${jwt.tokenSecret}") String tokenSecret,
      @Value("${jwt.disabledEmails:invalidemail@gmail.com}") String[] disabledEmails) {
    this.tokenSecret = tokenSecret;
    this.disabledEmails = disabledEmails;
  }

  public Authentication authenticate(String accessToken) {
    final Key signingKey = getKey();
    try {
      var jwtClaims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(accessToken).getBody();
      if (jwtClaims != null && jwtClaims.getExpiration().after(new Date())) {
        var user = new User(jwtClaims.getSubject(), "", of());
        return new JwtAuthentication(user, jwtClaims, getAuthorities(jwtClaims));
      }
    } catch (Exception ex) {
      throw new RuntimeException("Invalid Access Token", ex);
    }
    throw new RuntimeException("Invalid Access Token");
  }

  private Key getKey() {
    return new SecretKeySpec(
        Base64.getDecoder().decode(tokenSecret), SignatureAlgorithm.HS256.getJcaName());
  }

  @SuppressWarnings("unchecked")
  private List<GrantedAuthority> getAuthorities(Claims claims) {
    List<String> rolesFromJwt =
        Optional.ofNullable(claims.get("roles"))
            .map(List.class::cast)
            .orElse(Collections.emptyList());

    return rolesFromJwt.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
  }

  public void ensureJwtHasRequiredClaims(Authentication authentication) {
    if (!(authentication instanceof JwtAuthentication)) {
      return;
    }
    final Claims jwtClaims = ((JwtAuthentication) authentication).getJwtClaimsSet();
    if (jwtClaims != null) {
      ensureKeysExists(jwtClaims, ID, ORGANIZATION, SCOPE, IS_MOBILE, LOGIN_METHOD);
    }
  }

  private void ensureKeysExists(Claims jwtClaims, String... keys) {
    for (String key : keys) {
      if (!jwtClaims.containsKey(key)) {
        throw new RuntimeException("error.errorWithToken");
      }
    }
  }

  public void validateDisabledUsers(Authentication authentication) {
    if (!(authentication instanceof JwtAuthentication)) {
      return;
    }
    final Claims jwtClaims = ((JwtAuthentication) authentication).getJwtClaimsSet();

    if (jwtClaims != null && jwtClaims.containsKey(EMAIL) && disabledEmails != null) {
      String email = jwtClaims.get(EMAIL, String.class);
      if (Stream.of(disabledEmails).anyMatch(email::equalsIgnoreCase)) {
        throw new RuntimeException("error.errorWithToken");
      }
    }
  }
}
