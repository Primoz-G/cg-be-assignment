package com.example.store.controller;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.example.store.dto.ProductDto;
import com.example.store.error.ValidationError;
import com.example.store.error.ValidationErrorResponse;
import com.example.store.exception.ResourceNotFoundException;
import com.example.store.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests HTTP responses of {@link ProductController}.
 */
@WebMvcTest(controllers = ProductController.class)
class ProductControllerHttpTest {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private List<ProductDto> allProducts;

    @BeforeEach
    void setUp() {
        final ProductDto chips = new ProductDto(1L, "Chips", "Crispy!", 2.49f);
        final ProductDto cheese = new ProductDto(2L, "Cheese", "Mozzarella", 4.80f);
        allProducts = List.of(chips, cheese);

        // Mocks returns from the service layer
        when(productService.getProductById(1L)).thenReturn(chips);
        when(productService.getProductById(2L)).thenReturn(cheese);
        when(productService.getAllProducts()).thenReturn(allProducts);
    }

    @Test
    void testGetAllProducts() throws Exception {
        this.mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().string(OBJECT_MAPPER.writeValueAsString(allProducts)))
                .andExpect(MockMvcResultMatchers.header().string("content-type", "application/json"));
    }

    @Test
    void testGetAllProducts_invalidAcceptHeader() throws Exception {
        // Only application/json is supported
        this.mockMvc.perform(get("/products")
                        .accept(MediaType.MULTIPART_FORM_DATA_VALUE))
                .andExpect(status().isNotAcceptable());
    }

    @Test
    void testGetProductById() throws Exception {
        this.mockMvc.perform(get("/products/1"))
                .andExpect(status().isOk())
                .andExpect(content().string(OBJECT_MAPPER.writeValueAsString(allProducts.get(0))));
    }

    @Test
    void testGetProductById_notFound() throws Exception {
        this.mockMvc.perform(get("/products/5"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateProduct() throws Exception {
        final ProductDto productToAdd = new ProductDto(null, "Apple juice", "Homemade juice, 1L", 8.5f);
        final ProductDto savedProduct = new ProductDto(18L, "Apple juice", "Homemade juice, 1L", 8.5f);
        // Return is used for Location header
        when(productService.createProduct(any(ProductDto.class)))
                .thenReturn(savedProduct);

        this.mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(productToAdd)))
                .andExpect(status().isCreated())
                // NOTE: WebMvcTest only looks at this controller, so /api is not appended
                // ID should match the saved one
                .andExpect(header().string("Location", "http://localhost/products/18"));
    }

    @Test
    void testCreateProduct_invalidBody() throws Exception {
        this.mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content("{\"k\": \"v\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testCreateProduct_negativePrice() throws Exception {
        // Negative price -> should return bad request
        final ProductDto productToAdd = new ProductDto(null, "Gold", "Definitely real gold", -12345);

        final MvcResult mvcResult = this.mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(productToAdd)))
                .andExpect(status().isBadRequest())
                .andReturn();
        // Check the validation message
        final String response = mvcResult.getResponse().getContentAsString();
        final ValidationErrorResponse expectedResponse = new ValidationErrorResponse(Collections.singletonList(new ValidationError("price", "must be greater than or equal to 0")));
        assertEquals(OBJECT_MAPPER.writeValueAsString(expectedResponse), response);
    }

    @Test
    void testUpdateProduct() throws Exception {
        this.mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(allProducts.get(0))))
                .andExpect(status().isOk());
    }

    @Test
    void testUpdateProduct_invalidBody() throws Exception {
        // Product with missing name -> should return bad request
        final ProductDto invalidProductDto = new ProductDto(1L, "", "desc", 9.99f);
        final MvcResult mvcResult = this.mockMvc.perform(put("/products/1")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(invalidProductDto)))
                .andExpect(status().isBadRequest())
                .andReturn();
        // Check the validation messages
        final String response = mvcResult.getResponse().getContentAsString();
        final ValidationErrorResponse validationErrorResponse = OBJECT_MAPPER.readValue(response, ValidationErrorResponse.class);
        final List<ValidationError> expectedValidationErrors = new java.util.ArrayList<>(List.of(
                new ValidationError("name", "size must be between 2 and 100"),
                new ValidationError("name", "must not be blank")
        ));
        final List<ValidationError> diff = expectedValidationErrors.stream()
                .filter(validationError -> !validationErrorResponse.errors().contains(validationError))
                .toList();
        assertEquals(0, diff.size(), "The validation errors in the response are not as expected");
    }

    @Test
    void testUpdateProduct_conflictingIds() throws Exception {
        final ProductDto productDto = new ProductDto(100L, "invalid", "I don't exist...", 99.99f);

        // path variable and dto DO NOT match
        this.mockMvc.perform(put("/products/200")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(productDto)))
                .andExpect(status().isBadRequest());
        // Should fail in controller and not reach the service
        verify(productService, never()).updateProduct(anyLong(), any(ProductDto.class));
    }

    @Test
    void testUpdateProduct_missingResource() throws Exception {
        final ProductDto productDto = new ProductDto(999L, "invalid", "I don't exist...", 99.99f);
        when(productService.updateProduct(999L, productDto))
                .thenThrow(new ResourceNotFoundException("Product not found"));

        // path variable and dto match, so it continues to the service and throws there
        this.mockMvc.perform(put("/products/999")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(OBJECT_MAPPER.writeValueAsString(productDto)))
                .andExpect(status().isNotFound());
        verify(productService).updateProduct(anyLong(), any(ProductDto.class));
    }

    @Test
    void testDeleteProduct() throws Exception {
        this.mockMvc.perform(delete("/products/1"))
                .andExpect(status().isNoContent());
    }
}
