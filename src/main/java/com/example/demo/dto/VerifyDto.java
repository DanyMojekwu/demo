package com.example.demo.dto;

import jakarta.persistence.Column;

import java.util.Date;

public class VerifyDto {
    @Column(name = "verification code", nullable = false)
    private int verificationCode;

    public int getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(int verificationCode) {
        this.verificationCode = verificationCode;
    }
}
