package com.example.store.dto.mapper;

import com.example.store.dto.ProductDto;
import com.example.store.persistence.entity.Product;

import lombok.experimental.UtilityClass;

@UtilityClass
public final class ProductDtoMapper {

    // TODO: use mapstruct instead??

    public static Product fromDto(final ProductDto productDto) {
        return new Product(
                productDto.getId(),
                productDto.getName(),
                productDto.getDescription(),
                productDto.getPrice()
        );
    }

    public static ProductDto toDto(final Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice()
        );
    }
}
