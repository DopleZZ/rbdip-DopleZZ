package com.rbdip.bookstore.order;

import org.springframework.stereotype.Component;

@Component
public class CreateOrderValidator {

    public void validate(CreateOrderRequest request) {
        requireFilled(request.customerFullName(), "customerFullName is required");
        requireFilled(request.customerAddress(), "customerAddress is required");
        if (request.items() == null || request.items().isEmpty()) {
            throw new IllegalArgumentException("order must contain at least one item");
        }
    }

    private void requireFilled(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }
}
