package com.security.authentication.controller;

import com.security.authentication.dtos.request.ForgetAndResetPasswordDTO;
import com.security.authentication.dtos.request.SignupAndSigninRequestDTO;
import com.security.authentication.dtos.request.VerifyOtpRequestDTO;
import com.security.authentication.dtos.response.LoginResponseDTO;
import com.security.authentication.dtos.validators.ForgotPasswordGroup;
import com.security.authentication.dtos.validators.ResetPasswordGroup;
import com.security.authentication.dtos.validators.VerifyOtpGroup;
import com.security.authentication.model.User;
import com.security.authentication.service.AuthService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@Validated @RequestBody SignupAndSigninRequestDTO signupRequestDTO) {
        this.authService.signUp(signupRequestDTO);
        return ResponseEntity.ok(Map.of("Message", "Otp sent to your email " + signupRequestDTO.getEmail() + " verify the account"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<User> verifyOtp(@Validated @RequestBody VerifyOtpRequestDTO verifyOtpRequestDTO) {
        User user = this.authService.verifyOtp(verifyOtpRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@Validated @RequestBody SignupAndSigninRequestDTO login) {
        LoginResponseDTO token = authService.login(login);
        String accessToken = token.getAccessToken();
        String refreshToken = token.getRefreshToken();

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .httpOnly(true).secure(true).path("/").maxAge(7 * 24 * 60 * 10)
                .sameSite("strict").build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("token", accessToken));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refresh(@CookieValue(name = "refreshToken") String refreshToken) {
        LoginResponseDTO token = authService.refresh(refreshToken);
        String newAccessToken = token.getAccessToken();
        String newRefreshToken = token.getRefreshToken();
        ResponseCookie cookie = ResponseCookie.from("refreshToken", newRefreshToken)
                .httpOnly(true).secure(true).path("/").maxAge(7 * 24 * 60 * 10)
                .sameSite("strict").build();

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("token", newAccessToken));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(
            @Validated(ForgotPasswordGroup.class) @RequestBody ForgetAndResetPasswordDTO dto) {
        authService.forgotPassword(dto);
        return ResponseEntity.ok().body(Map.of("message", "OTP send to your registered email, " + dto.getEmail() + " check that out"));
    }

    @PostMapping("/verify-reset-password")
    public ResponseEntity<Map<String, String>> verifyResetPasswordOtp(
            @Validated(VerifyOtpGroup.class) @RequestBody ForgetAndResetPasswordDTO dto) {
        authService.verifyResetPasswordOtp(dto);
        // success logic
        return ResponseEntity.ok().body(Map.of("", ""));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(
            @Validated(ResetPasswordGroup.class) @RequestBody ForgetAndResetPasswordDTO dto) {
        authService.resetPassword(dto);
        // success logic
        return ResponseEntity.ok().body(Map.of("", ""));
    }

}
