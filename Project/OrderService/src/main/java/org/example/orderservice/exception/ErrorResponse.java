package org.example.orderservice.exception;

import java.time.LocalDateTime;

public record ErrorResponse(
        int statusCode,
        String message,
        LocalDateTime timestamp
) {
}