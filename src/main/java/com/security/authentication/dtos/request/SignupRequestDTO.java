package com.security.authentication.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;

public class SignupRequestDTO {

    @NotNull(message = "Email is required")
    @Email(message = "Provide a valid email")
    private String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
