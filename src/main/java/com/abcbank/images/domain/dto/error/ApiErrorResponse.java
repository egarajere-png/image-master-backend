package com.abcbank.images.domain.dto.error;

import lombok.*;

import java.time.OffsetDateTime;
import java.util.Map;

/** Uniform error shape returned by every handler in GlobalExceptionHandler 
 *  - timestamp, HTTP status, message, request path, and optional per-field validation errors. */

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiErrorResponse {

    private OffsetDateTime timestamp;

    private int status;

    private String error;

    private String message;

    private String path;

    private Map<String, String> validationErrors;
}
