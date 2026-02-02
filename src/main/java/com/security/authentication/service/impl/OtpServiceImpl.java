package com.security.authentication.service.impl;

import com.security.authentication.exception.OtpRestrictionException;
import com.security.authentication.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;

public class OtpServiceImpl implements OtpService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    public static Map<String, String> otpKeys(String email) {
        return Map.of(
                "otpCount", "OTP_COUNT:" + email, // 6 count per day
                "otpSpamLock", "OTP_SPAM_LOCK:" + email, //if 3 failed attempts within 1hr
                "otpSpamCount", "OTP_SPAM_COUNT:" + email, //if 3 request  within 1hr block for 2 hr
                "otpCoolDown", "OTP_COOLDOWN:" + email, //one minute waiting before new otp
                "otpBlock", "OTP_BLOCK:" + email, //block if requests more than 6 times in 24 hrs
                "otpWrongCount", "OTP_WRONG_COUNT:" + email, //block if requests more than 6 times in 24 hrs
                "otp", "OTP:" + email
        );
    }

    @Override
    public void checkOtpRestriction(String email) {
        Map<String, String> keys = otpKeys(email);
        if(Boolean.TRUE.equals(redisTemplate.hasKey(keys.get("otpCoolDown")))){
            throw new OtpRestrictionException("Wait one minute before new OTP requests");
        }
        if(Boolean.TRUE.equals(redisTemplate.hasKey(keys.get("otpSpamLock")))){
            throw new OtpRestrictionException("Multiple failed attempts try again 2 hour later");
        }
        if(Boolean.TRUE.equals(redisTemplate.hasKey(keys.get("otpBlock")))){
            throw new OtpRestrictionException("OTP request limit exceed ,try again after 24 hours");
        }
    }

    @Override
    public void trackOtpRequests(String email) {
        Map<String, String> keys = otpKeys(email);
        String lockKey = keys.get("otpSpamLock");
        String countKey = keys.get("otpSpamCount");
        String otpTotalCountKey = keys.get("otpCount");

        // 1. Handle Daily Limit (6 per 24h)
        Long totalCount = redisTemplate.opsForValue().increment(otpTotalCountKey);
        if (totalCount != null && totalCount == 1) {
            redisTemplate.expire(otpTotalCountKey, Duration.ofHours(24));
        }
        if (totalCount != null && totalCount > 6) {
            throw new OtpRestrictionException("Daily limit reached. Try again after 24 hr");
        }

        // 2. Handle Spam Lock (Already blocked check)
        if (Boolean.TRUE.equals(redisTemplate.hasKey(lockKey))) {
            throw new OtpRestrictionException("Multiple Otp requests, try again after 2 hr");
        }

        // 3. Handle Spam Counter (The 2-hour window)
        Long spamCount = redisTemplate.opsForValue().increment(countKey);
        if (spamCount != null && spamCount == 1) {
            redisTemplate.expire(countKey, Duration.ofHours(2));
        }

        // 4. Trigger Spam Lock if needed
        if (spamCount != null && spamCount > 3) {
            redisTemplate.opsForValue().set(lockKey, "TRUE", Duration.ofHours(2));
            throw new OtpRestrictionException("Multiple Otp requests, try again after 2 hr");
        }
    }

}
