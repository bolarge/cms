package com.software.finatech.lslb.cms.service.config;

import com.software.finatech.jjwt.JwtAuthenticationEntryPoint;
import com.software.finatech.jjwt.JwtAuthenticationProvider;
import com.software.finatech.jjwt.JwtHeaderTokenExtractor;
import com.software.finatech.jjwt.JwtTokenValidator;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtConfig {

    @Bean(name = "jwtAuthenticationProvider")
    public JwtAuthenticationProvider jwtAuthenticationProvider() {
        return new JwtAuthenticationProvider();
    }

    @Bean(name = "jwtTokenValidator")
    public JwtTokenValidator jwtTokenValidator() {
        return new JwtTokenValidator();
    }

    @Bean(name = "jwtHeaderTokenExtractor")
    public JwtHeaderTokenExtractor jwtHeaderTokenExtractor() {
        return new JwtHeaderTokenExtractor();
    }

    @Bean(name = "jwtAuthenticationEntryPoint")
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint();
    }
}
