package com.security.authentication.service.impl;

import com.security.authentication.dtos.request.SignupRequestDTO;
import com.security.authentication.dtos.request.VerifyOtpRequestDTO;
import com.security.authentication.exception.AlreadyExistsException;
import com.security.authentication.exception.OtpRestrictionException;
import com.security.authentication.model.User;
import com.security.authentication.repository.AuthRepository;
import com.security.authentication.service.AuthService;
import com.security.authentication.service.MailService;
import com.security.authentication.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Map;

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

        otpService.checkOtpRestriction(email);
        otpService.trackOtpRequests(email);

        String encodedPassword=passwordEncoder.encode(password);

        User user=new User();
        user.setEmail(email);
        user.setPassword(encodedPassword);

        authRepository.save(user);
        String otp = otpService.getOtp();
        mailService.mailSender(email, otp);
        redisTemplate.opsForValue().set("OTP:" + email, otp, Duration.ofMinutes(5));
        redisTemplate.opsForValue().set("OTP_COOLDOWN:" + email, "success", Duration.ofMinutes(1));
    }

    @Override
    public User verifyOtp(VerifyOtpRequestDTO verifyOtpRequestDTO) {
        String email = verifyOtpRequestDTO.getEmail();
        User user = authRepository.findByEmail(email);
        if (user==null) {
            redisTemplate.delete(OtpServiceImpl.otpKeys(email).values());
            throw new AlreadyExistsException("User does not exist with this mail "+email+"try again later");
        }
        boolean isMatch = this.otpService.verifyOtp(verifyOtpRequestDTO);

        if(!isMatch){
            throw new OtpRestrictionException("Invalid otp");
        }
        user.setVerified(true);
        return authRepository.save(user);
    }
}
