package com.mobylab.springbackend.config.security;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsFilterConfiguration implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173") // Portul de Vite/React
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // Am adăugat și PATCH pentru fulfill
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
