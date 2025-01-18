package com.example.store.dto;

import java.io.Serializable;

import com.example.store.persistence.entity.Product;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * DTO for {@link Product}
 */
@Data
@AllArgsConstructor
public class ProductDto implements Serializable {
    private final Long id;
    private final String name;
    private final String description;
    private final float price;
}
