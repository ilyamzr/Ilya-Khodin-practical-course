package org.example.authenticationservice.dto;

import lombok.Data;

@Data
public class TokenValidationRequest {
    private String token;

    public String getToken() {
        return token;
    }
}