package org.example.authenticationservice.dto;

import org.example.authenticationservice.entity.Role;

import java.util.Set;

public class RegisterRequest {
    private String username;
    private String password;
    private Set<Role> roles;

    public String getUsername() {
        return username;
    }
    public String getPassword() {
        return password;
    }
    public Set<Role> getRoles() {
        return roles;
    }
}