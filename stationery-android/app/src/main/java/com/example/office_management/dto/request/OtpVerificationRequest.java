package com.example.office_management.dto.request;

public class OtpVerificationRequest {
    private String email;
    private int otp;

    public OtpVerificationRequest(String email, int otp){
        this.email = email;
        this.otp = otp;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getOtp() {
        return otp;
    }

    public void setOtp(int otp) {
        this.otp = otp;
    }
}
