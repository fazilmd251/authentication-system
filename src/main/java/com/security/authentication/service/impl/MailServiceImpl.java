package com.security.authentication.service.impl;

import com.security.authentication.builder.MailBuilder;
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
    public void mailSender( MailBuilder mailBuilder) {
        SimpleMailMessage message=new SimpleMailMessage();

        message.setFrom(mailBuilder.getFrom());
        message.setTo(mailBuilder.getTo());
        message.setSubject(mailBuilder.getSubject());
        message.setText(mailBuilder.getText());

        mailSender.send(message);
    }
}
