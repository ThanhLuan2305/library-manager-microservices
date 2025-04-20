package com.project.libmanage.activity_log_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;


@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.project.libmanage.library_common.client")
@SpringBootApplication(scanBasePackages = {
		"com.project.libmanage.activity_log_service",
		"com.project.libmanage.library_common"
})
public class ActivityLogServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(ActivityLogServiceApplication.class, args);
	}

}
