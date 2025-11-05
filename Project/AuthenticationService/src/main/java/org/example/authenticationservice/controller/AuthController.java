package org.example.authenticationservice.controller;

import org.example.authenticationservice.dto.*;
import org.example.authenticationservice.entity.UserRoles;
import org.example.authenticationservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest request) {
        try {
            UserRoles newUser = authService.registerUser(request.getUsername(), request.getPassword(), request.getRoles());
            return new ResponseEntity<>("User registered successfully: " + newUser.getUsername(), HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>("Registration failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            AuthService.AuthTokens tokens = authService.authenticateAndGenerateTokens(request.getUsername(), request.getPassword());
            return ResponseEntity.ok(new AuthResponse(tokens.accessToken, tokens.refreshToken));
        } catch (UsernameNotFoundException | BadCredentialsException e) {
            return new ResponseEntity<>("Invalid username or password", HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>("Login failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request) {
        try {
            AuthService.AuthTokens tokens = authService.refreshTokens(request.getRefreshToken());
            return ResponseEntity.ok(new AuthResponse(tokens.accessToken, tokens.refreshToken));
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>("Token refresh failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/validate")
    public ResponseEntity<Boolean> validateToken(@RequestBody TokenValidationRequest request) {
        boolean isValid = authService.validateToken(request.getToken());
        return ResponseEntity.ok(isValid);
    }

    @GetMapping("/test-json")
    public ResponseEntity<TestDto> testJson() {
        return ResponseEntity.ok(new TestDto("value1", "value2"));
    }
}