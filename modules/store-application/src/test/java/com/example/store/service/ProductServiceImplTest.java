package com.example.store.service;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.example.store.dto.ProductDto;
import com.example.store.exception.ResourceNotFoundException;
import com.example.store.persistence.entity.Product;
import com.example.store.persistence.repository.ProductRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Class contains unit tests for {@link ProductServiceImpl}.
 */
class ProductServiceImplTest {

    private ProductRepository productRepository;
    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productRepository = mock(ProductRepository.class);
        productService = new ProductServiceImpl(productRepository);

        final Product banana = new Product(1L, "Banana", "1kg of bananas", 1.8f);
        final Product orange = new Product(2L, "Orange", "1kg of oranges", 3.2f);

        when(productRepository.findAll()).thenReturn(List.of(banana, orange));
        when(productRepository.findById(1L)).thenReturn(Optional.of(banana));
        when(productRepository.findById(2L)).thenReturn(Optional.of(orange));
    }

    @Test
    void testGetAllProducts() {
        final List<ProductDto> allProducts = productService.getAllProducts();
        assertEquals(2, allProducts.size());
        verify(productRepository).findAll();
    }

    @Test
    void testGetProductById() {
        final ProductDto productById = productService.getProductById(1L);
        assertNotNull(productById);
        assertEquals(1L, productById.getId());
        verify(productRepository).findById(1L);
    }

    @Test
    void testGetProductById_notFound() {
        assertThrowsExactly(ResourceNotFoundException.class, () -> productService.getProductById(99L));
        verify(productRepository).findById(99L);
    }

    @Test
    void testCreateProduct() {
        when(productRepository.save(any(Product.class)))
                .thenAnswer(i -> {
                    // Mock ID which is created by the db
                    final Product product = i.getArgument(0, Product.class);
                    product.setId(8L);
                    return product;
                });

        final ProductDto productDto = new ProductDto(null, "Watermelon", "Very watery", 2.5f);
        final ProductDto createdProduct = productService.createProduct(productDto);
        assertEquals(8L, createdProduct.getId(), "ID should be added on save() and returned in the Dto");
    }

    @Test
    void testUpdateProduct() {
        // Banana exists in db
        when(productRepository.existsById(1L)).thenReturn(true);
        // Return identical entity on save
        when(productRepository.save(any(Product.class)))
                .thenAnswer(i -> i.getArgument(0, Product.class));

        final ProductDto productDto = new ProductDto(1L, "Banana", "Now 50% off!", 0.9f);
        final ProductDto updatedProduct = productService.updateProduct(1L, productDto);
        verify(productRepository).save(any());
        assertEquals(productDto, updatedProduct);
    }

    @Test
    void testUpdateProduct_notFound() {
        when(productRepository.existsById(50L)).thenReturn(false);

        final ProductDto productDto = new ProductDto(50L, "Raspberry", "200g bag", 3.2f);
        assertThrowsExactly(ResourceNotFoundException.class, () -> productService.updateProduct(1L, productDto));
        // Verify that save was never called
        verify(productRepository, never()).save(any());
    }

    @Test
    void testDeleteProduct() {
        when(productRepository.existsById(5L)).thenReturn(true);
        productService.deleteProduct(5L);
        verify(productRepository).deleteById(5L);
    }

    @Test
    void testDeleteProduct_notFound() {
        // repository.existsById returns false
        assertThrowsExactly(ResourceNotFoundException.class, () -> productService.deleteProduct(88L));
        verify(productRepository, never()).deleteById(88L);
    }
}