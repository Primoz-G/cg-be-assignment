package com.example.store.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.store.dto.ProductDto;
import com.example.store.dto.mapper.ProductDtoMapper;
import com.example.store.exception.ResourceNotFoundException;
import com.example.store.persistence.entity.Product;
import com.example.store.persistence.repository.ProductRepository;

import lombok.extern.slf4j.Slf4j;

/**
 * Default implementation of {@link ProductService}.
 */
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(final ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Gets all product from the database.
     *
     * @return list of all product DTOs, empty list if none are found
     */
    @Override
    public List<ProductDto> getAllProducts() {
        return this.productRepository.findAll()
                .stream()
                .map(ProductDtoMapper::toDto)
                .toList();
    }

    /**
     * Gets the product by the specified ID.
     *
     * @param id product ID
     * @return product DTO
     * @throws ResourceNotFoundException if product does not exist in the database (handler will return 404 not found)
     */
    @Override
    public ProductDto getProductById(final Long id) {
        return this.productRepository.findById(id)
                .map(ProductDtoMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Product with id " + id + " not found"));
    }

    /**
     * Adds the specified product to the database.
     *
     * @param productDto product DTO
     * @return created product DTO
     */
    @Override
    public ProductDto createProduct(final ProductDto productDto) {
        final Product entity = this.productRepository.save(ProductDtoMapper.fromDto(productDto));
        log.debug("Created new product with id {}", entity.getId());
        return ProductDtoMapper.toDto(entity);
    }

    /**
     * Updates the specified product in the database.
     *
     * @param id         ID of the product
     * @param productDto product DTO
     * @return updated product DTO
     * @throws ResourceNotFoundException if product does not exist in the database (handler will return 404 not found)
     */
    @Override
    public ProductDto updateProduct(final Long id, final ProductDto productDto) {
        if (!this.productRepository.existsById(id)) {
            log.error("Trying to update non-existing product with id {}", id);
            throw new ResourceNotFoundException("Failed update - product with id " + id + " does not exist");
        }
        final Product updatedEntity = this.productRepository.save(ProductDtoMapper.fromDto(productDto));
        log.debug("Updated product with id {}", updatedEntity.getId());
        return ProductDtoMapper.toDto(updatedEntity);
    }

    /**
     * Deletes the product with the specified ID from the database.
     *
     * @param id product ID
     * @throws ResourceNotFoundException if product does not exist in the database (handler will return 404 not found)
     */
    @Override
    public void deleteProduct(final Long id) {
        if (!this.productRepository.existsById(id)) {
            log.error("Trying to delete non-existing product with id {}", id);
            throw new ResourceNotFoundException("Failed deletion - product with id " + id + " does not exist");
        }
        this.productRepository.deleteById(id);
        log.debug("Deleted product with id {}", id);
    }

}
