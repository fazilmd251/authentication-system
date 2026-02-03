package com.security.authentication.service;


import com.security.authentication.dtos.request.SignupRequestDTO;
import com.security.authentication.dtos.request.VerifyOtpRequestDTO;

public interface AuthService {

    //signup only receive email
    //we will send the otp to this email
    void signUp(SignupRequestDTO authRequestDTO);
    void verifyOtp(VerifyOtpRequestDTO verifyOtpRequestDTO);
}
