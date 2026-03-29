package com.mobylab.springbackend.controller;

import com.mobylab.springbackend.service.AuthService;
import com.mobylab.springbackend.dto.LoginDto;
import com.mobylab.springbackend.dto.LoginResponseDto;
import com.mobylab.springbackend.dto.RegisterDto;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register") // Am simplificat scrierea din RequestMapping
    public ResponseEntity<?> register(@RequestBody RegisterDto registerDto) {
        logger.info("Request to register user {}", registerDto.getEmail());
        authService.register(registerDto);
        logger.info("Successfully registered user {}", registerDto.getEmail());
        return new ResponseEntity<>("User registered", HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginDto loginDto) {
        logger.info("Request to login for user {}", loginDto.getEmail());

        // Apelăm service-ul și primim direct pachetul complet
        LoginResponseDto response = authService.login(loginDto);

        logger.info("Successfully logged in user {}", loginDto.getEmail());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/token")
    public ResponseEntity<?> validateToken() {
        UserDetails user = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        logger.info("Request to validate token for user {}", user.getUsername());
        String email = user.getUsername();
        logger.info("Successfully validated token for user {}", user.getUsername());
        return new ResponseEntity<>(email, HttpStatus.OK);
    }
}