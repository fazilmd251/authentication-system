package com.security.authentication.service;

import com.security.authentication.builder.MailBuilder;

public interface MailService {
    void mailSender(MailBuilder mailBuilder);
}
