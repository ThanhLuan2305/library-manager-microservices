package com.project.libmanage.auth_service.service;



import com.project.libmanage.library_common.dto.request.LoginDetailRequest;

import java.time.Instant;

public interface ILoginDetailService {
    void createLoginDetail(LoginDetailRequest loginRequest);

    void disableLoginDetailById(String jti);

    void updateLoginDetailIsEnable(String jti, Instant expTime);

    void deleteLoginDetailByUser(Long userId);
}
