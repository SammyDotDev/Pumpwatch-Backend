package com.devnaza.pumpwatch.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class ErrorResponse {

    private String message;
    private String status;
    private int statusCode;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Map<String, String> fieldErrors; // field -> message
    private long timestamp;
}
