package com.security.authentication.service;

import com.security.authentication.dtos.request.VerifyOtpRequestDTO;

import java.util.Map;

public interface OtpService {
void checkOtpRestriction(String email);
void trackOtpRequests(String email);
String getOtp();
    boolean verifyOtp(VerifyOtpRequestDTO verifyOtpRequestDTO,String key);
}
