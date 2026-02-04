package com.security.authentication.service.impl;

import com.security.authentication.dtos.request.SignupAndSigninRequestDTO;
import com.security.authentication.dtos.request.VerifyOtpRequestDTO;
import com.security.authentication.exception.AlreadyExistsException;
import com.security.authentication.exception.OtpRestrictionException;
import com.security.authentication.exception.UserNotFoundException;
import com.security.authentication.model.User;
import com.security.authentication.repository.AuthRepository;
import com.security.authentication.service.AuthService;
import com.security.authentication.service.MailService;
import com.security.authentication.service.OtpService;
import com.security.authentication.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private AuthRepository authRepository;
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    @Autowired
    private OtpService otpService;
    @Autowired
    private MailService mailService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;


    @Override
    public void signUp(SignupAndSigninRequestDTO authRequestDTO) {
        String email = authRequestDTO.getEmail();
        String password = authRequestDTO.getPassword();

        // Check if user exists
        User existingUser = authRepository.findByEmail(email);

        //if exist not verified send otp otherwise return exception
        if (existingUser != null) {
            if (existingUser.isVerified()) {
                throw new AlreadyExistsException("This email is already registered. Please login.");
            }
            existingUser.setPassword(passwordEncoder.encode(password));
            authRepository.save(existingUser);
        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPassword(passwordEncoder.encode(password));
            authRepository.save(newUser);
        }

        otpService.checkOtpRestriction(email);
        otpService.trackOtpRequests(email);

        String otp = otpService.getOtp();
        mailService.mailSender(email, otp);
        // Store in Redis
        redisTemplate.opsForValue().set("OTP:" + email, otp, Duration.ofMinutes(5));
        redisTemplate.opsForValue().set("OTP_COOLDOWN:" + email, "success", Duration.ofMinutes(1));
    }

    @Override
    public User verifyOtp(VerifyOtpRequestDTO verifyOtpRequestDTO) {
        String email = verifyOtpRequestDTO.getEmail();
        User user = authRepository.findByEmail(email);
        if (user == null) {
            redisTemplate.delete(OtpServiceImpl.otpKeys(email).values());
            throw new AlreadyExistsException("User does not exist with this mail " + email + "try again later");
        }
        boolean isMatch = this.otpService.verifyOtp(verifyOtpRequestDTO);

        if (!isMatch) {
            throw new OtpRestrictionException("Invalid otp");
        }
        user.setVerified(true);
        return authRepository.save(user);
    }

    @Override
    public String login(SignupAndSigninRequestDTO loginDto) {
        String email = loginDto.getEmail();
        String password = loginDto.getPassword();

        User user = authRepository.findByEmail(email);

        if (user == null) {
            throw new BadCredentialsException("Invalid email or password");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        return jwtService.generateToken(user);
    }

}

//public void signUp(SignupAndSigninRequestDTO authRequestDTO) {
//    String email = authRequestDTO.getEmail();
//    String password = authRequestDTO.getPassword();
//
//    User existingUser=authRepository.findByEmail(email);
//
//    if(existingUser!=null&&!existingUser.isVerified()){
//        this.notVerified(email);return;
//    }
//
//
//
//
//    otpService.checkOtpRestriction(email);
//    otpService.trackOtpRequests(email);
//
//    String encodedPassword = passwordEncoder.encode(password);
//
//    User user = new User();
//    user.setEmail(email);
//    user.setPassword(encodedPassword);
//
//    authRepository.save(user);
//    String otp = otpService.getOtp();
//    mailService.mailSender(email, otp);
//    redisTemplate.opsForValue().set("OTP:" + email, otp, Duration.ofMinutes(5));
//    redisTemplate.opsForValue().set("OTP_COOLDOWN:" + email, "success", Duration.ofMinutes(1));
//}
