package com.project.libmanage.library_common.client;

import com.project.libmanage.library_common.config.AuthenticaitonRequestIntercepter;
import com.project.libmanage.library_common.dto.request.LogActionRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "activity-log-service", path = "/internal", configuration = {AuthenticaitonRequestIntercepter.class})
public interface ActivityLogFeignClient {
    @PostMapping("/activity-log")
    public void logAction(@RequestBody LogActionRequest logActionRequest);
}
