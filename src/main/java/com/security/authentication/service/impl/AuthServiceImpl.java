package com.security.authentication.service.impl;

import com.security.authentication.dtos.request.SignupRequestDTO;
import com.security.authentication.dtos.request.VerifyOtpRequestDTO;
import com.security.authentication.exception.AlreadyExistsException;
import com.security.authentication.repository.AuthRepository;
import com.security.authentication.service.AuthService;
import com.security.authentication.service.MailService;
import com.security.authentication.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AuthServiceImpl implements AuthService {
    private final AuthRepository authRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private OtpService otpService;
    @Autowired
    private MailService mailService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public AuthServiceImpl(AuthRepository authRepository) {
        this.authRepository = authRepository;
    }

    @Override
    public void signUp(SignupRequestDTO authRequestDTO) {
        String email = authRequestDTO.getEmail();
        String password = authRequestDTO.getPassword();


        String encodedPassword = this.passwordEncoder.encode(password);


        if (encodedPassword != null)
            redisTemplate.opsForValue().set("PASSWORD:" + email, encodedPassword, Duration.ofMinutes(30));


        otpService.checkOtpRestriction(email);
        otpService.trackOtpRequests(email);
        String otp = otpService.getOtp();
        mailService.mailSender(email, otp);
        redisTemplate.opsForValue().set("OTP:" + email, otp, Duration.ofMinutes(5));
        redisTemplate.opsForValue().set("OTP_COOLDOWN:" + email, "success", Duration.ofMinutes(1));
    }

    @Override
    public void verifyOtp(VerifyOtpRequestDTO verifyOtpRequestDTO) {
        String email = verifyOtpRequestDTO.getEmail();
        boolean exists = authRepository.existsByEmail(email);
        if (exists) {
            redisTemplate.delete(OtpServiceImpl.otpKeys(email).values());
            throw new AlreadyExistsException("User already exists with this email");
        }
        boolean isMatch = this.otpService.verifyOtp(verifyOtpRequestDTO);

        if(isMatch){
            // save user
            //return user email
        }

    }
}
