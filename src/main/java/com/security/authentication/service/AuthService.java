package com.security.authentication.service;


import com.security.authentication.dtos.request.SignupAndSigninRequestDTO;
import com.security.authentication.dtos.request.VerifyOtpRequestDTO;
import com.security.authentication.dtos.response.LoginResponseDTO;
import com.security.authentication.model.User;

public interface AuthService {

    //signup only receive email
    //we will send the otp to this email
    void signUp(SignupAndSigninRequestDTO authRequestDTO);
    User verifyOtp(VerifyOtpRequestDTO verifyOtpRequestDTO);
    LoginResponseDTO login(SignupAndSigninRequestDTO loginDto);
}
