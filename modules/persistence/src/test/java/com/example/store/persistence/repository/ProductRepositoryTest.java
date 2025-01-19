package com.example.store.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import com.example.store.persistence.entity.Product;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Rollback(false)
@DataJpaTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @Order(1)
    void testSave() {
        final Product product = new Product(null, "Chair", "Wooden chair", 35f);
        final Product savedProduct = productRepository.save(product);
        assertNotNull(savedProduct.getId());
    }

    @Test
    @Order(2)
    void testFindById() {
        final Optional<Product> product = productRepository.findById(1L);
        assertTrue(product.isPresent());
    }

    @Test
    @Order(3)
    void testUpdate() {
        final Product updatedProduct = productRepository.save(new Product(1L, "Chair", "Plastic chair", 19.99f));
        assertEquals("Plastic chair", updatedProduct.getDescription());
        assertEquals(19.99f, updatedProduct.getPrice());
    }

    @Test
    @Order(4)
    void testFindAll() {
        final List<Product> entities = productRepository.findAll();
        assertEquals(1, entities.size());
        // Check for updated entity
        assertEquals("Plastic chair", entities.get(0).getDescription());
    }

    @Test
    @Rollback
    void testSave_invalidProduct() {
        // Invalid product -> ConstraintViolationException should be thrown with constraint violation messages
        final String longString = RandomStringUtils.insecure().next(1001);
        final Product product = new Product(null, longString, longString, -1f);
        final ConstraintViolationException exception = assertThrowsExactly(ConstraintViolationException.class, () -> productRepository.save(product));
        final List<String> expectedMessages = List.of(
                "size must be between 2 and 100",       // name
                "size must be between 2 and 1000",      // description
                "must be greater than or equal to 0"    // price
        );
        assertTrue(exception.getConstraintViolations().stream()
                        .map(ConstraintViolation::getMessage)
                        .toList()
                        .containsAll(expectedMessages),
                "Expected constraint violations not found when saving invalid product");
    }
}
