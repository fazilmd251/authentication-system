package com.security.authentication.builder;

public class MailBuildHelper {

    public MailBuilder buildSignupMail(String toEmail,String otp){
        return new MailBuilder(
                "your-app-name <" + "your-email@gmail.com" + ">",
                toEmail,
                "Your Secure OTP Verification Code"
                ,"Hello,\n\n" +
                "Your verification code is: " + otp + "\n\n" +
                "This code is valid for 5 minutes. If you did not request this, please ignore this email.\n\n" +
                "Regards,\nYour App Team"
        );
    }

    public MailBuilder buildForgotPasswordMail(String toEmail,String otp){
        return new MailBuilder(
                "your-app-name <" + "your-email@gmail.com" + ">",
                toEmail,
                "Your Secure OTP Verification Code"
                ,"Hello,\n\n" +
                "Your verification code is: " + otp + "\n\n" +
                "This code is valid for 5 minutes. If you did not request this, please ignore this email.\n\n" +
                "Regards,\nYour App Team"
        );
    }

}
