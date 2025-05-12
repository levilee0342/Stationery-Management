package com.project.stationery_be_server.service.impl;

import com.project.stationery_be_server.Error.AuthErrorCode;
import com.project.stationery_be_server.dto.request.EmailRequest;
import com.project.stationery_be_server.exception.AppException;
import com.project.stationery_be_server.service.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public String sendSimpleMail(EmailRequest details) {
        try {
            // Tạo một đối tượng MimeMessage
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(sender);
            helper.setTo(details.getRecipient());
            helper.setSubject("[Stationary's P] OTP to verify your account");

            // Tạo nội dung email dạng HTML
            String htmlContent = "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: auto; "
                    + "padding: 20px; border: 1px solid #ddd; border-radius: 10px;'>"
                    + "<h2 style='color: #333; text-align: center;'>Verify Your Account</h2>"
                    + "<p style='font-size: 16px; text-align: center;'>Use the OTP below to verify your account. "
                    + "This OTP is valid for <strong>5 minutes</strong>.</p>"
                    + "<div style='background: #f4f4f4; padding: 15px; text-align: center; border-radius: 5px; "
                    + "font-size: 24px; font-weight: bold;'>" + details.getOtp() + "</div>"
                    + "<p style='text-align: center;'>If you did not request this, please ignore this email.</p>"
                    + "<hr style='border: 0; height: 1px; background: #ddd;'>"
                    + "<p style='font-size: 12px; text-align: center; color: #888;'>© 2024 Digital Store. All rights reserved.</p>"
                    + "</div>";

            helper.setText(htmlContent, true);

            // Gửi email
            javaMailSender.send(message);
            return "Mail Sent Successfully...";

        } catch (MessagingException e) {
            e.printStackTrace();
            throw new AppException(AuthErrorCode.SEND_MAIL_FAILD);
        }
    }
}
