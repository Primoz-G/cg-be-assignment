package com.example.store.error;

import java.io.Serializable;
import java.util.List;

public record ValidationErrorResponse(List<ValidationError> errors) implements Serializable {
}
