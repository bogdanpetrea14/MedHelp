package com.mobylab.springbackend.service;

import com.mobylab.springbackend.config.security.JwtGenerator;
import com.mobylab.springbackend.dto.LoginResponseDto;
import com.mobylab.springbackend.entity.User;
import com.mobylab.springbackend.enums.UserRole;
import com.mobylab.springbackend.enums.UserStatus;
import com.mobylab.springbackend.exception.BadRequestException;
import com.mobylab.springbackend.repository.UserRepository;
import com.mobylab.springbackend.dto.LoginDto;
import com.mobylab.springbackend.dto.RegisterDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtGenerator jwtGenerator;

    public void register(RegisterDto registerDto) {
        if(userRepository.existsUserByEmail(registerDto.getEmail())) {
            throw new BadRequestException("Email is already used");
        }

        // Setam rolul default ca PACIENT, status ACTIVE
        userRepository.save(new User()
                .setEmail(registerDto.getEmail())
                .setPassword(passwordEncoder.encode(registerDto.getPassword()))
                .setRole(UserRole.PATIENT)
                .setStatus(UserStatus.ACTIVE));
    }

    public LoginResponseDto login(LoginDto loginDto) {
        // 1. Încercăm autentificarea
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getEmail(),
                        loginDto.getPassword()));

        // 2. Setăm contextul (important pentru sesiunea curentă)
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Generăm token-ul
        String token = jwtGenerator.generateToken(authentication);

        // 4. Căutăm user-ul ca să îi aflăm rolul (știm că există deja, altfel pica la pasul 1)
        User user = userRepository.findUserByEmail(loginDto.getEmail())
                .orElseThrow(() -> new BadRequestException("User not found"));

        // 5. Returnăm DTO-ul complet
        return new LoginResponseDto()
                .setToken(token)
                .setRole(user.getRole())
                .setExpiresIn(3600); // 1 oră
    }
}