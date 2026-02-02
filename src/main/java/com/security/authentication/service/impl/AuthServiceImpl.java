package com.security.authentication.service.impl;

import com.security.authentication.dtos.request.SignupRequestDTO;
import com.security.authentication.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public String signUp(SignupRequestDTO authRequestDTO) {
        //verify the otp restrictions-
        //send otp to this email
        //redisTemplate.opsForValue().set();
        return "";
    }
}
