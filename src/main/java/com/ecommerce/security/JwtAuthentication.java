package com.ecommerce.security;

import io.jsonwebtoken.Claims;
import java.util.Collection;
import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

@EqualsAndHashCode(callSuper = true)
public class JwtAuthentication extends AbstractAuthenticationToken {

  private final Object principal;
  private Claims jwtClaimsSet;

  public JwtAuthentication(
      Object principal, Claims jwtClaimsSet, Collection<? extends GrantedAuthority> authorities) {
    super(authorities);
    this.principal = principal;
    this.jwtClaimsSet = jwtClaimsSet;
    super.setAuthenticated(true);
  }

  public Object getCredentials() {
    return null;
  }

  public Object getPrincipal() {
    return this.principal;
  }

  public Claims getJwtClaimsSet() {
    return this.jwtClaimsSet;
  }
}
