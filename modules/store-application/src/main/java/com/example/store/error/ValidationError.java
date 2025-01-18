package com.example.store.error;

import java.io.Serializable;

public record ValidationError(String field, String message) implements Serializable {
}
