package com.example.store;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;

import com.example.store.dto.ProductDto;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Integration tests for product CRUD methods.
 * Tests all methods, however validation errors are tested in ProductControllerTest,
 * since they don't reach the other layers.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD) // Makes sure the db is recreated for each test
class ProductIT {

    // Products as they should be in the db after the initial state is set in BeforeEach
    private static final ProductDto PRODUCT_DTO_1 = new ProductDto(1L, "Pencil", "Wooden pencil", 0.5f);
    private static final ProductDto PRODUCT_DTO_2 = new ProductDto(2L, "Eraser", "Super eraserTM", 1.2f);

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        // Set initial state
        final ProductDto productDto1 = new ProductDto(null, "Pencil", "Wooden pencil", 0.5f);
        final ProductDto productDto2 = new ProductDto(null, "Eraser", "Super eraserTM", 1.2f);
        restTemplate.postForEntity("/api/products", productDto1, ProductDto.class);
        restTemplate.postForEntity("/api/products", productDto2, ProductDto.class);
    }

    @Test
    void testGetAllProducts() {
        final ResponseEntity<ProductDto[]> response = restTemplate.getForEntity("/api/products", ProductDto[].class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        final ProductDto[] allProducts = response.getBody();
        assertNotNull(allProducts);
        assertEquals(2, allProducts.length);
        assertEquals(Arrays.asList(allProducts), List.of(PRODUCT_DTO_1, PRODUCT_DTO_2));
    }

    @Test
    void testGetProductById() {
        final ResponseEntity<ProductDto> response = restTemplate.getForEntity("/api/products/1", ProductDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        final ProductDto productDto = response.getBody();
        assertNotNull(productDto);
        assertEquals(PRODUCT_DTO_1, productDto);
    }

    @Test
    void testAddProduct() {
        // Add product and check the same values + ID are returned
        final ProductDto productDto = new ProductDto(null, "Box", "Small box", 10.0f);
        final ResponseEntity<ProductDto> response = restTemplate.postForEntity("/api/products", productDto, ProductDto.class);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        final ProductDto savedProductDto = response.getBody();
        assertNotNull(savedProductDto);
        assertNotNull(savedProductDto.getId());
        assertEquals("Box", savedProductDto.getName());
        assertEquals("Small box", savedProductDto.getDescription());
        assertEquals(10.0f, savedProductDto.getPrice());
    }

    @Test
    void testUpdateProduct() {
        final ProductDto productDto = new ProductDto(1L, "Pencil 2.0", "Metal pencil", 3.4f);
        final HttpEntity<ProductDto> requestEntity = new HttpEntity<>(productDto);
        final ResponseEntity<ProductDto> response = restTemplate.exchange("/api/products/1", HttpMethod.PUT, requestEntity, ProductDto.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(productDto, response.getBody());
    }

    @Test
    void testUpdateProduct_whenResourceNotFound() {
        final ProductDto productDto = new ProductDto(5L, "Pencil 2.0", "Metal pencil", 3.4f);
        final HttpEntity<ProductDto> requestEntity = new HttpEntity<>(productDto);
        final ResponseEntity<ProductDto> response = restTemplate.exchange("/api/products/5", HttpMethod.PUT, requestEntity, ProductDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void testDeleteProduct() {
        final ResponseEntity<ProductDto> deleteResponse = restTemplate.exchange("/api/products/1", HttpMethod.DELETE, null, ProductDto.class);
        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());

        // Check that product was deleted
        final ResponseEntity<ProductDto> getResponse = restTemplate.getForEntity("/api/products/1", ProductDto.class);
        assertEquals(HttpStatus.NOT_FOUND, getResponse.getStatusCode());
    }

    @Test
    void testDeleteProduct_whenResourceNotFound() {
        final ResponseEntity<ProductDto> response = restTemplate.exchange("/api/products/5", HttpMethod.DELETE, null, ProductDto.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}
