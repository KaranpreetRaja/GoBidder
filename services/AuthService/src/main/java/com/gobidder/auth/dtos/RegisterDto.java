package com.gobidder.auth.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.NotBlank;

public class RegisterDto {
    @NotBlank(message = "Full name is required.")
    private String fullName;

    @Email(message = "Invalid email format.")
    @NotBlank(message = "Email is required.")
    private String email;

    @NotBlank(message = "Password is required.")
    private String password;

    @NotBlank(message = "Card number is required.")
    @Pattern(regexp = "\\d{16}", message = "Card number must be 16 digits.")
    private String cardNumber;

    @NotBlank(message = "CSV is required.")
    @Pattern(regexp = "\\d{3}", message = "CSV must be 3 digits.")
    private String csv;

    @NotBlank(message = "Expiration date is required.")
    @Pattern(regexp = "\\d{2}/\\d{2}", message = "Expiration date must be in MM/YY format.")
    private String expirationDate;

    @NotBlank(message = "Billing address is required.")
    private String billingAddress;

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getCsv() {
        return csv;
    }

    public String getExpirationDate() {
        return expirationDate;
    }

    public String getBillingAddress() {
        return billingAddress;
    }
}