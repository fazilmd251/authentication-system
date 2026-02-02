package com.security.authentication.service.impl;

import com.security.authentication.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;


@Service
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void mailSender(String toEmail, String otp) {
        SimpleMailMessage message=new SimpleMailMessage();

        message.setFrom("your-app-name <" + "your-email@gmail.com" + ">");
        message.setTo(toEmail);
        message.setSubject("Your Secure OTP Verification Code");
        message.setText("Hello,\n\n" +
                "Your verification code is: " + otp + "\n\n" +
                "This code is valid for 5 minutes. If you did not request this, please ignore this email.\n\n" +
                "Regards,\nYour App Team");

        mailSender.send(message);
    }
}
