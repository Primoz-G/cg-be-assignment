package com.example.store.error;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ValidationErrorResponseTest {

    @Test
    void testJsonSerialization() throws JsonProcessingException {
        final ValidationErrorResponse validationErrorResponse = new ValidationErrorResponse(List.of(
                new ValidationError("name", "is too long"),
                new ValidationError("description", "is too long")
        ));
        final ObjectMapper objectMapper = new ObjectMapper();
        final String expectedJson = "{\"errors\":[{\"field\":\"name\",\"message\":\"is too long\"},{\"field\":\"description\",\"message\":\"is too long\"}]}";
        final String json = objectMapper.writeValueAsString(validationErrorResponse);
        assertEquals(expectedJson, json);
    }
}
