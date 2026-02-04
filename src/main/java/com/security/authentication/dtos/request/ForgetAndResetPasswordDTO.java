package com.security.authentication.dtos.request;

import com.security.authentication.dtos.validators.ForgotPasswordGroup;
import com.security.authentication.dtos.validators.ResetPasswordGroup;
import com.security.authentication.dtos.validators.VerifyOtpGroup;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ForgetAndResetPasswordDTO {

    @NotNull(groups = {ForgotPasswordGroup.class, VerifyOtpGroup.class, ResetPasswordGroup.class}, message = "Email is required")
    @Email(groups = {ForgotPasswordGroup.class, VerifyOtpGroup.class, ResetPasswordGroup.class}, message = "Provide a valid email")
    private String email;

    // Validated only during OTP verification
    @NotNull(groups = VerifyOtpGroup.class, message = "OTP is required")
    @Size(min = 6, max = 6, groups = VerifyOtpGroup.class, message = "OTP must be 6 digits")
    private String otp;

    // Validated only during final password reset
    @NotNull(groups = ResetPasswordGroup.class, message = "Password is required")
    @Size(min = 5, max = 50, groups = ResetPasswordGroup.class, message = "Password must be between 5 and 50 characters")
    private String newPassword;

    // Validated only during final password reset
    @NotNull(groups = ResetPasswordGroup.class, message = "Reset token is required")
    private String resetToken;

    public ForgetAndResetPasswordDTO() {
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getResetToken() {
        return resetToken;
    }

    public void setResetToken(String resetToken) {
        this.resetToken = resetToken;
    }
}
