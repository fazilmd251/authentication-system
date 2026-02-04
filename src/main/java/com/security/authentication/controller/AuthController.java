package com.security.authentication.controller;

import com.security.authentication.dtos.request.SignupAndSigninRequestDTO;
import com.security.authentication.dtos.request.VerifyOtpRequestDTO;
import com.security.authentication.model.User;
import com.security.authentication.service.AuthService;
import org.springframework.http.HttpStatus;
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
        String token = authService.login(login);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(Map.of("token", token));
    }

}
