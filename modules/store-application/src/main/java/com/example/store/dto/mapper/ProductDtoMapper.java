package com.example.store.dto.mapper;

import org.jetbrains.annotations.NotNull;

import com.example.store.dto.ProductDto;
import com.example.store.persistence.entity.Product;

import lombok.experimental.UtilityClass;

/**
 * Mapper between {@link ProductDto} and {@link Product}.
 */
@UtilityClass
public final class ProductDtoMapper {

    /**
     * Maps {@link ProductDto} to {@link Product}.
     *
     * @param productDto {@link ProductDto}
     * @return {@link Product}
     */
    @NotNull
    public static Product fromDto(@NotNull final ProductDto productDto) {
        return new Product(
                productDto.getId(),
                productDto.getName(),
                productDto.getDescription(),
                productDto.getPrice()
        );
    }

    /**
     * Maps {@link Product} to {@link ProductDto}.
     *
     * @param product {@link Product}
     * @return {@link ProductDto}
     */
    @NotNull
    public static ProductDto toDto(@NotNull final Product product) {
        return new ProductDto(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getPrice()
        );
    }
}
