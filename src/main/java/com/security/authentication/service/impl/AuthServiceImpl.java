package com.security.authentication.service.impl;

import com.security.authentication.builder.MailBuildHelper;
import com.security.authentication.builder.MailBuilder;
import com.security.authentication.dtos.request.ForgetAndResetPasswordDTO;
import com.security.authentication.dtos.request.SignupAndSigninRequestDTO;
import com.security.authentication.dtos.request.VerifyOtpRequestDTO;
import com.security.authentication.dtos.response.LoginResponseDTO;
import com.security.authentication.exception.AlreadyExistsException;
import com.security.authentication.exception.OtpRestrictionException;
import com.security.authentication.exception.UserNotFoundException;
import com.security.authentication.model.User;
import com.security.authentication.repository.AuthRepository;
import com.security.authentication.service.AuthService;
import com.security.authentication.service.MailService;
import com.security.authentication.service.OtpService;
import com.security.authentication.utils.ExtractClaims;
import com.security.authentication.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

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
    @Autowired
    private ExtractClaims extractClaims;


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
        MailBuilder mail = new MailBuildHelper().buildSignupMail(email, otp);
        mailService.mailSender(mail);
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
        String redisOtpKey = "OTP:" + email;
        boolean isMatch = this.otpService.verifyOtp(verifyOtpRequestDTO, redisOtpKey);

        if (!isMatch) {
            throw new OtpRestrictionException("Invalid otp");
        }
        user.setVerified(true);
        return authRepository.save(user);
    }

    @Override
    public LoginResponseDTO login(SignupAndSigninRequestDTO loginDto) {
        String email = loginDto.getEmail();
        String password = loginDto.getPassword();

        User user = authRepository.findByEmail(email);

        if (user == null) {
            throw new BadCredentialsException("Invalid email or password");
        }
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid email or password");
        }

        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        redisTemplate.opsForValue().set("REFRESH:" + email, refreshToken, Duration.ofHours(1));

        return new LoginResponseDTO(accessToken, refreshToken);
    }

    public LoginResponseDTO refresh(String refreshToken) {
        var sub = extractClaims.extractAllClaims(refreshToken);
        String email = sub.getSubject();
        String storedToken = (String) redisTemplate.opsForValue().get("REFRESH:" + email);
        if (storedToken == null || !storedToken.equals(refreshToken)) {
            redisTemplate.delete("REFRESH:" + email);
            throw new BadCredentialsException("Security Alert: Session compromised. Please login again.");
        }

        redisTemplate.delete("REFRESH:" + email);

        User user = authRepository.findByEmail(email);

        String newRefreshToken = jwtService.generateRefreshToken(user);
        String newAccessToken = jwtService.generateToken(user);

        redisTemplate.opsForValue().set("REFRESH:" + email, newRefreshToken);

        return new LoginResponseDTO(newAccessToken, newRefreshToken);
    }

    @Override
    public void forgotPassword(ForgetAndResetPasswordDTO dto) {

        String email = dto.getEmail();

        User user = authRepository.findByEmail(email);

        if (user == null) {
            throw new BadCredentialsException("Invalid email");
        }
        otpService.checkOtpRestriction(email);
        otpService.trackOtpRequests(email);

        String otp = otpService.getOtp();
        MailBuilder mail = new MailBuildHelper().buildForgotPasswordMail(email, otp);
        mailService.mailSender(mail);
        redisTemplate.opsForValue().set("FORGOT_PASSWORD_OTP:" + email, otp, Duration.ofMinutes(5));
    }

    @Override
    public String verifyResetPasswordOtp(ForgetAndResetPasswordDTO dto) {
        String email = dto.getEmail();
        String otp = dto.getOtp();

        String redisOtpKey = "FORGOT_PASSWORD_OTP:" + email;
        VerifyOtpRequestDTO object = new VerifyOtpRequestDTO(otp,email);
        otpService.verifyOtp(object, redisOtpKey);

        //this token is needed to reset password
        String resetToken = UUID.randomUUID().toString();

        redisTemplate.opsForValue().set("RESET_TOKEN:" + email, resetToken, Duration.ofMinutes(5));

        return resetToken; // Send this back to the frontend
    }

    public void resetPassword(ForgetAndResetPasswordDTO dto) {
        String email = dto.getEmail();
        String resetTokenFromUser = dto.getResetToken();

        String storedToken = (String) redisTemplate.opsForValue().get("RESET_TOKEN:" + email);

        if (storedToken == null || !storedToken.equals(resetTokenFromUser)) {
            throw new BadCredentialsException("Invalid or expired reset session.");
        }

        User user = authRepository.findByEmail(email);
        if (user == null) throw new UserNotFoundException("User not found");

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        authRepository.save(user);

        redisTemplate.delete("RESET_TOKEN:" + email);


        redisTemplate.delete("REFRESH:" + email);
    }
}


