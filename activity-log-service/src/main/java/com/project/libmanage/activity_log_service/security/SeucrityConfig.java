package com.project.libmanage.activity_log_service.security;

import com.project.libmanage.library_common.security.BaseSecurityConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
@Slf4j
public class SeucrityConfig extends BaseSecurityConfig {

    @Value("#{'${security.public-endpoints-post}'.split(',')}")
    private String[] publicEndPoints;

    @Value("#{'${security.public-endpoints-get}'.split(',')}")
    private String[] publicEndPointsGet;

    @Value("#{'${security.permissions.admin_role}'.split(',')}")
    private String[] adminEndPoint;

    @Value("#{'${security.permissions.user_role}'.split(',')}")
    private String[] userEndPoint;
    private final CustomDecoder customDecoder;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        return buildSecurityChain(httpSecurity, customDecoder, publicEndPoints, publicEndPointsGet, adminEndPoint, userEndPoint);
    }
}
