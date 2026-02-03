package com.security.authentication.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class SignupRequestDTO {

    @NotNull(message = "Email is required")
    @Email(message = "Provide a valid email")
    private String email;

    @NotNull(message = "Password is required")
    @Size(min = 5,message = "Password must be at least 5 characters")
    @Size(max = 50,message = "Password cannot exceed 50 characters")
    private String password;



    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
