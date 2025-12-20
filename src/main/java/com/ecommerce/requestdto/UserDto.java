package com.ecommerce.requestdto;

import lombok.Builder;
import lombok.NonNull;

public record UserDto() {

  public record UserSignUpRequest(
      @NonNull String firstName,
      @NonNull String lastName,
      @NonNull String userName,
      @NonNull String password,
      String email,
      @NonNull Long mobileNumber) {}

  @Builder
  public record UserLoginRequest(@NonNull String userName, @NonNull String password) {}
}
