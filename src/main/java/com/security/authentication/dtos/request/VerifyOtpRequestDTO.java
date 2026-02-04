package com.security.authentication.dtos.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class VerifyOtpRequestDTO {

    @NotNull(message = "OTP is required")
    @Size(min = 6, max = 6, message = "OTP must be 6 digits")
    private String otp;

    @NotNull(message = "Email is required")
    @Email(message = "Provide a valid email")
    private String email;

    public VerifyOtpRequestDTO() {
    }

    public VerifyOtpRequestDTO(String otp, String email) {
        this.otp = otp;
        this.email = email;
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

}
