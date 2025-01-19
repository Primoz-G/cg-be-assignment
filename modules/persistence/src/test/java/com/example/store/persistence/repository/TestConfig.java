package com.example.store.persistence.repository;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import com.example.store.persistence.entity.Product;

/**
 * Config for repository tests.
 */
@SpringBootConfiguration
@EnableJpaRepositories(basePackageClasses = ProductRepository.class)
@EntityScan(basePackageClasses = Product.class)
public class TestConfig {
}
