package com.security.authentication.controller;

import com.security.authentication.dtos.request.SignupRequestDTO;
import com.security.authentication.service.AuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/test")
    public String test(){
       //redisTemplate.opsForValue().set("status","works", Duration.ofSeconds(30));
        return "works";
    }

    @PostMapping("signup")
    public String signup(@Validated @RequestBody SignupRequestDTO signupRequestDTO){
        this.authService.signUp(signupRequestDTO);
        return "success";
    }

}
