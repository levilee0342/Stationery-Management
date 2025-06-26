package com.project.stationery_be_server.controller;

import com.project.stationery_be_server.dto.request.SupportEmailRequest;
import com.project.stationery_be_server.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @PostMapping("/support")
    public ResponseEntity<String> sendSupportEmail(@RequestBody SupportEmailRequest request) {
        String response = emailService.sendSupportEmail(request);
        return ResponseEntity.ok(response);
    }
}
