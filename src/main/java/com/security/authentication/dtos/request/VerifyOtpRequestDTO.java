package com.security.authentication.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class VerifyOtpRequestDTO {

    @NotNull(message = "OTP is required")
    @Size(max = 6,message ="Otp must be 6 digit" )
    private String otp;

    @NotNull(message = "Email is required")
    @Email(message = "Provide a valid email")
    private String email;

    @NotNull(message = "Password is required")
    private String password;

    public VerifyOtpRequestDTO() {
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

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
