package com.ecommerce.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
public class JwtAuthFilter extends OncePerRequestFilter {

  private static final String BEARER_TOKEN_PREFIX = "Bearer ";
  private final AccessTokenProcessor accessTokenProcessor;

  @Value("${jwt.headerString:Authorization}")
  private String headerString;

  public JwtAuthFilter(AccessTokenProcessor accessTokenProcessor) {
    this.accessTokenProcessor = accessTokenProcessor;
  }

  @Override
  protected void doFilterInternal(
      @NonNull HttpServletRequest request,
      @NonNull HttpServletResponse response,
      @NonNull FilterChain filterChain)
      throws ServletException, IOException {
    Authentication authentication;

    try {
      String accessToken = request.getHeader(headerString);
      if (accessToken != null && accessToken.startsWith(BEARER_TOKEN_PREFIX)) {
        authentication = this.accessTokenProcessor.authenticate(getBearerToken(accessToken));
        accessTokenProcessor.ensureJwtHasRequiredClaims(authentication);
        accessTokenProcessor.validateDisabledUsers(authentication);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception exception) {
      SecurityContextHolder.clearContext();
      throw new RuntimeException("error.errorWithToken", exception);
    }

    filterChain.doFilter(request, response);
  }

  private String getBearerToken(String token) {
    return token.startsWith(BEARER_TOKEN_PREFIX)
        ? token.substring(BEARER_TOKEN_PREFIX.length())
        : token;
  }
}
