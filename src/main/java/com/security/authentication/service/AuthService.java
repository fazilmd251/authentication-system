package com.security.authentication.service;


import com.security.authentication.dtos.request.SignupRequestDTO;

public interface AuthService {

    //signup only receive email
    //we will send the otp to this email
    String signUp(SignupRequestDTO authRequestDTO);
}
