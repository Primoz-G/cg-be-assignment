package com.example.store.persistence.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.example.store.persistence.entity.Product;

@Repository
@Transactional
public interface ProductRepository extends JpaRepository<Product, Long> {

}
