package com.example.employeedirectoryproject.service.serviceImpl;

import com.example.employeedirectoryproject.dto.EmailDTO;
import com.example.employeedirectoryproject.service.EmailSenderService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailSenderServiceImpl implements EmailSenderService {

    private final JavaMailSender emailSender;

    private final SpringTemplateEngine templateEngine;

    @Override
    public void sendHtmlMessage(EmailDTO emailDto)  throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        context.setVariables(emailDto.getProperties());
        helper.setFrom(emailDto.getFrom());
        helper.setTo(emailDto.getTo());
        helper.setSubject(emailDto.getSubject());
        String html = templateEngine.process(emailDto.getTemplate(), context);
        helper.setText(html, true);
        log.info("Sending email: {} with html body: {}", emailDto, html);
        emailSender.send(message);
    }
}
