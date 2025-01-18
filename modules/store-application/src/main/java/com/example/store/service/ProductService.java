package com.example.store.service;

import java.util.List;

import com.example.store.dto.ProductDto;

public interface ProductService {

    List<ProductDto> getAllProducts();

    ProductDto getProductById(Long id);

    ProductDto createProduct(ProductDto productDto);

    ProductDto updateProduct(Long id, ProductDto productDto);

    void deleteProduct(Long id);
}
