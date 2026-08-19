package com.example.accommodation.exception;

import java.time.LocalDateTime;
import java.util.List;

public record ErrorResponse(LocalDateTime timestamp, int status, List<String> errors) {
    public ErrorResponse(int status, List<String> errors) {
        this(LocalDateTime.now(), status, errors);
    }
}
