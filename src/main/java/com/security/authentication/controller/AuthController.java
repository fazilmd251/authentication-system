package com.security.authentication.controller;

import com.security.authentication.dtos.request.SignupRequestDTO;
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

    @GetMapping("/test")
    public String test() {
        //redisTemplate.opsForValue().set("status","works", Duration.ofSeconds(30));
        return "works";
    }

    @PostMapping("/signup")
    public ResponseEntity<Map<String, String>> signup(@Validated @RequestBody SignupRequestDTO signupRequestDTO) {
        this.authService.signUp(signupRequestDTO);
        return ResponseEntity.ok(Map.of("Message", "Otp sent to your email " + signupRequestDTO.getEmail() + " verify the account"));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<User> verifyOtp(@Validated @RequestBody VerifyOtpRequestDTO verifyOtpRequestDTO) {
        User user = this.authService.verifyOtp(verifyOtpRequestDTO);

        // Return the user object with a 201 Created or 200 OK status
        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

}
