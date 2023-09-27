package com.example.employeedirectoryproject.service;

import com.example.employeedirectoryproject.dto.EmailDTO;
import jakarta.mail.MessagingException;

public interface EmailSenderService {
    void sendHtmlMessage(EmailDTO emailDto) throws MessagingException;
}
