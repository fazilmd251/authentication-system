package com.security.authentication.service;


import com.security.authentication.dtos.request.SignupRequestDTO;
import com.security.authentication.dtos.request.VerifyOtpRequestDTO;
import com.security.authentication.model.User;

public interface AuthService {

    //signup only receive email
    //we will send the otp to this email
    void signUp(SignupRequestDTO authRequestDTO);
    User verifyOtp(VerifyOtpRequestDTO verifyOtpRequestDTO);
}
