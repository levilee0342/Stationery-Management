package com.project.stationery_be_server.service;


import com.project.stationery_be_server.dto.request.EmailRequest;
import com.project.stationery_be_server.dto.request.SupportEmailRequest;

public interface EmailService {
    // Method
    // To send a simple email
    String sendSimpleMail(EmailRequest request);
    String sendSupportEmail(SupportEmailRequest request);
}
