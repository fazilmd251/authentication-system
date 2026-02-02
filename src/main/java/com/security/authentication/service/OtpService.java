package com.security.authentication.service;

import java.util.Map;

public interface OtpService {
void checkOtpRestriction(String email);
void trackOtpRequests(String email);
}
