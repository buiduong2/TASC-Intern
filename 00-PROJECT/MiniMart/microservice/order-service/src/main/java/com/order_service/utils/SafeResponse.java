package com.order_service.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class SafeResponse<T, F> {
    private Boolean success;
    private int status;
    private T successBody; // Parsed object or raw string
    private F failureBody;
    private String rawBody;
}
