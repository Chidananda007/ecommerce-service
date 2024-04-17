package com.amazon.requestdto;

import lombok.NonNull;

public record UserDto() {

    public record UserSignUpDto(
            @NonNull
            String userFirstName,
            @NonNull
            String userLastName,
            @NonNull
            String userName,
            @NonNull
            String password,
            @NonNull
            Long mobileNumber){}


        public record UserFetch(
                @NonNull
                String userName,
                @NonNull
                String password
        ){}

}
