package com.security.authentication.service.impl;

import com.security.authentication.dtos.request.SignupRequestDTO;
import com.security.authentication.service.AuthService;
import com.security.authentication.service.MailService;
import com.security.authentication.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private OtpService otpService;

    @Autowired
    private MailService mailService;

    @Override
    public void signUp(SignupRequestDTO authRequestDTO) {
        String email=authRequestDTO.getEmail();
        otpService.checkOtpRestriction(email);
        otpService.trackOtpRequests(email);
        String otp=otpService.getOtp();
        mailService.mailSender(email,otp);
        redisTemplate.opsForValue().set("OTP:"+email,otp, Duration.ofMinutes(5));
        redisTemplate.opsForValue().set("OTP_COOLDOWN:"+email,"success",Duration.ofMinutes(1));
    }
}
