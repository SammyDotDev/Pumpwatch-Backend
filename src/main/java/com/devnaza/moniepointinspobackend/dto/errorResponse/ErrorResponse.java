package com.devnaza.moniepointinspobackend.dto.errorResponse;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Data
@Builder
public class ErrorResponse {

    private String message;
    private String status;
    private int statusCode;
    private Map<String, String> fieldErrors; // field -> message
    private long timestamp;
}
