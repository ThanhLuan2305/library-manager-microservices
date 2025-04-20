package com.project.libmanage.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication(scanBasePackages = {
		"com.project.libmanage.user_service",
		"com.project.libmanage.library_common"
})
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.project.libmanage.library_common.client")
public class UserServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
