package com.example.store.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.store.dto.ProductDto;
import com.example.store.dto.mapper.ProductDtoMapper;
import com.example.store.exception.ResourceNotFoundException;
import com.example.store.persistence.entity.Product;
import com.example.store.persistence.repository.ProductRepository;

/**
 * Default implementation of {@link ProductService}.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    @Override
    public List<ProductDto> getAllProducts() {
        return this.productRepository.findAll()
                .stream()
                .map(ProductDtoMapper::toDto)
                .toList();
    }

    @Override
    public ProductDto getProductById(final Long id) {
        return this.productRepository.findById(id)
                .map(ProductDtoMapper::toDto)
                .orElse(null);
    }

    @Override
    public ProductDto createProduct(final ProductDto productDto) {
        final Product entity = this.productRepository.save(ProductDtoMapper.fromDto(productDto));
        return ProductDtoMapper.toDto(entity);
    }

    @Override
    public ProductDto updateProduct(final Long id, final ProductDto productDto) {
        if (!this.productRepository.existsById(id)) {
            // Handler will return 404 not found
            throw new ResourceNotFoundException("Product with id " + id + " does not exist");
        }
        final Product updatedEntity = this.productRepository.save(ProductDtoMapper.fromDto(productDto));
        return ProductDtoMapper.toDto(updatedEntity);
    }

    @Override
    public void deleteProduct(final Long id) {
        this.productRepository.deleteById(id);
    }

}
