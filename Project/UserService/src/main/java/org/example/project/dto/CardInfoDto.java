package org.example.project.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class CardInfoDto {

    private Long id;

    @NotNull(message = "User ID is required")
    private Long userId;

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "\\d{4}-\\d{4}-\\d{4}-\\d{4}", message = "Card number must be in format XXXX-XXXX-XXXX-XXXX")
    private String number;

    @NotBlank(message = "Card holder is required")
    @Size(max = 255, message = "Holder name must not exceed 255 characters")
    private String holder;

    @NotNull(message = "Expiration date is required")
    @FutureOrPresent(message = "Expiration date must not be in the past")
    private LocalDate expirationDate;

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getNumber() { return number; }
    public String getHolder() { return holder; }
    public LocalDate getExpirationDate() { return expirationDate; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setNumber(String number) { this.number = number; }
    public void setHolder(String holder) { this.holder = holder; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }
}