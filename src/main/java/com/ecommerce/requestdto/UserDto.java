package com.ecommerce.requestdto;

import lombok.NonNull;

public record UserDto() {

  public record UserSignUpRequest(
      @NonNull String userFirstName,
      @NonNull String userLastName,
      @NonNull String userName,
      @NonNull String password,
      String email,
      @NonNull Long mobileNumber) {}

  public record UserFetch(@NonNull String userName, @NonNull String password) {}
}
