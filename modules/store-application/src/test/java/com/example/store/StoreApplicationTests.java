package com.example.store;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.store.controller.ProductController;
import com.example.store.persistence.repository.ProductRepository;
import com.example.store.service.ProductService;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@EnableAutoConfiguration(exclude = {DataSourceAutoConfiguration.class})
class StoreApplicationTests {

    @MockitoBean
    private ProductRepository productRepository;

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductController productController;

    @Test
    void contextLoads() {
        assertNotNull(productController);
        assertNotNull(productService);
    }

}
