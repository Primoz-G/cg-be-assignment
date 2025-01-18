package com.example.store.dto;

import java.io.Serializable;

import com.example.store.persistence.entity.Product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for {@link Product}
 */
@Data
@AllArgsConstructor
public class ProductDto implements Serializable {
    private final Long id;
    @NotBlank
    @Size(min = 2, max = 100)
    private final String name;
    @NotBlank
    @Size(min = 2, max = 1000)
    private final String description;
    @NotNull
    @PositiveOrZero
    private final float price;
}
