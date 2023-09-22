package com.example.employeedirectoryproject.service;

import com.example.employeedirectoryproject.dto.EmailDto;
import jakarta.mail.MessagingException;

public interface EmailSenderService {
    void sendHtmlMessage(EmailDto emailDto) throws MessagingException;
}
