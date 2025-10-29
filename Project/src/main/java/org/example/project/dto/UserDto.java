package org.example.project.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class UserDto {

    private Long id;

    @NotBlank(message = "Name is required")
    @Size(max = 255, message = "Name must not exceed 255 characters")
    private String name;

    @NotBlank(message = "Surname is required")
    @Size(max = 255, message = "Surname must not exceed 255 characters")
    private String surname;

    @NotNull(message = "Birth date is required")
    @Past(message = "Birth date must be in the past")
    private LocalDate birthDate;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email must not exceed 255 characters")
    private String email;

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getSurname() { return surname; }
    public LocalDate getBirthDate() { return birthDate; }
    public String getEmail() { return email; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setSurname(String surname) { this.surname = surname; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }
    public void setEmail(String email) { this.email = email; }
}