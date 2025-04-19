package com.project.libmanage.book_service.entity.client;

import com.project.libmanage.library_common.dto.response.LoginDetailResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "auth-service", path = "/internal")
public interface AuthFeignClient {
    @GetMapping("/login-details/{jti}")
    LoginDetailResponse getLoginDetail(String jti);
}
