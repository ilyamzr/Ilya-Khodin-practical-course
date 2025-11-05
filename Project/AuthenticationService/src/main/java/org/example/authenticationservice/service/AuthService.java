package org.example.authenticationservice.service;

import org.example.authenticationservice.entity.Role;
import org.example.authenticationservice.entity.UserRoles;
import org.example.authenticationservice.repository.UserRolesRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService {

    private final UserRolesRepository userRolesRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;

    public AuthService(UserRolesRepository userRolesRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager, UserDetailsService userDetailsService) {
        this.userRolesRepository = userRolesRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
    }

    public UserRoles registerUser(String username, String password, Set<Role> roles) {
        if (userRolesRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("User with this username already exists.");
        }

        String salt = UUID.randomUUID().toString();
        String hashedPassword = passwordEncoder.encode(password + salt);

        UserRoles userRoles = new UserRoles(
                username,
                hashedPassword,
                salt,
                roles != null && !roles.isEmpty() ? roles : Collections.singleton(Role.USER)
        );

        return userRolesRepository.save(userRoles);
    }

    public AuthTokens authenticateAndGenerateTokens(String username, String password) {
        UserRoles userRoles = userRolesRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        if (!passwordEncoder.matches(password + userRoles.getSalt(), userRoles.getPassword())) {
            throw new BadCredentialsException("Invalid username or password");
        }
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String accessToken = jwtService.generateAccessToken(userDetails);
        String refreshToken = jwtService.generateRefreshToken(userDetails);
        return new AuthTokens(accessToken, refreshToken);
    }

    public AuthTokens refreshTokens(String refreshToken) {
        if (!jwtService.validateToken(refreshToken, userDetailsService.loadUserByUsername(jwtService.extractUsername(refreshToken)))) {
            throw new IllegalArgumentException("Invalid or expired refresh token.");
        }

        String username = jwtService.extractUsername(refreshToken);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);

        String newAccessToken = jwtService.generateAccessToken(userDetails);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails); // Генерируем новый Refresh Token

        return new AuthTokens(newAccessToken, newRefreshToken);
    }

    public boolean validateToken(String token) {
        try {
            String username = jwtService.extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            return jwtService.validateToken(token, userDetails);
        } catch (Exception e) {
            return false;
        }
    }

    public static class AuthTokens {
        public final String accessToken;
        public final String refreshToken;

        public AuthTokens(String accessToken, String refreshToken) {
            this.accessToken = accessToken;
            this.refreshToken = refreshToken;
        }
    }
}