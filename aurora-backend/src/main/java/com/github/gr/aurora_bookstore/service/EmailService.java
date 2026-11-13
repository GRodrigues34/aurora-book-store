package com.github.gr.aurora_bookstore.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    private final ObjectProvider<JavaMailSender> mailSenderProvider;

    @Value("${spring.mail.host:}")
    private String mailHost;

    @Value("${api.app.mail-from:noreply@aurorabookstore.local}")
    private String mailFrom;

    @Value("${api.app.frontend-url:http://localhost:5173}")
    private String frontendUrl;

    public EmailService(ObjectProvider<JavaMailSender> mailSenderProvider) {
        this.mailSenderProvider = mailSenderProvider;
    }

    public void sendVerificationEmail(String toEmail, String username, String token) {
        String verificationUrl = frontendUrl + "/verify-email?token=" + token;
        JavaMailSender mailSender = mailSenderProvider.getIfAvailable();

        if (mailSender == null || mailHost == null || mailHost.isBlank()) {
            log.info("[DEV EMAIL FALLBACK] SMTP not configured. Verification link for {} ({}): {}",
                    username, toEmail, verificationUrl);
            return;
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setFrom(mailFrom);
            helper.setTo(toEmail);
            helper.setSubject("Confirm your Aurora Bookstore account");
            helper.setText(buildVerificationHtml(username, verificationUrl), true);
            mailSender.send(message);
            log.info("Verification email sent to {}", toEmail);
        } catch (MessagingException e) {
            log.error("Failed to send verification email to {}: {}", toEmail, e.getMessage());
            throw new RuntimeException("Failed to send verification email", e);
        }
    }

    private String buildVerificationHtml(String username, String verificationUrl) {
        String safeName = username != null ? username : "Reader";
        return """
                <div style="font-family: Georgia, serif; max-width: 560px; margin: 0 auto; padding: 32px 24px; background-color: #FAF8F5; color: #18141F; border: 1px solid #E8E2D9; border-radius: 12px;">
                  <h1 style="font-size: 24px; margin: 0 0 12px; color: #4A1A6B;">Aurora Bookstore</h1>
                  <p style="font-family: sans-serif; font-size: 15px; line-height: 1.6; margin: 0 0 20px;">
                    Hello %s, thank you for joining Aurora Bookstore. Please confirm your email address to activate your account.
                  </p>
                  <a href="%s" style="display: inline-block; padding: 12px 24px; background-color: #6B21A8; color: #FFFFFF; text-decoration: none; border-radius: 999px; font-family: sans-serif; font-size: 14px; font-weight: 600;">
                    Confirm Email Address
                  </a>
                  <p style="font-family: sans-serif; font-size: 12px; color: #6B6574; margin: 24px 0 0; line-height: 1.5;">
                    If the button does not work, copy and paste this link into your browser:<br/>
                    <span style="word-break: break-all;">%s</span>
                  </p>
                </div>
                """.formatted(safeName, verificationUrl, verificationUrl);
    }
}
