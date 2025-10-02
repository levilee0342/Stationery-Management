package com.example.office_management.dto.request;

public class EmailRequest {
    private String recipient;
    private Integer otp;

    public EmailRequest(String recipient, Integer otp){
        this.recipient = recipient;
        this.otp = otp;
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }
}
