package com.project.libmanage.auth_service.service;


import com.project.libmanage.auth_service.entity.OtpVerification;
import com.project.libmanage.library_common.constant.OtpType;

public interface IOtpVerificationService {
    void createOtp(OtpVerification otpVerification, boolean isPhone);
    void deleteOtp(String contactInfo, OtpType type, boolean isPhone);
    boolean verifyOtp(String otp, String contactInfo, OtpType type, boolean isPhone);
}
