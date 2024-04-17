package com.amazon.responsedto;

import lombok.Builder;

public record ProductResponseDto() {

    @Builder
    public record ProductDetailsResponseDto(
            Long id, String productName, String productCategory, Double productPrice
    ){}


}
