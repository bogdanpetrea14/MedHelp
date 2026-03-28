package com.mobylab.springbackend.config;

import com.mobylab.springbackend.entity.User;
import com.mobylab.springbackend.enums.UserRole;
import com.mobylab.springbackend.enums.UserStatus;
import com.mobylab.springbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ApplicationInitializer implements CommandLineRunner {

    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Value("${admin.username}")
    private String adminUsername;
    @Value("${admin.password}")
    private String adminPassword;
    @Value("${admin.email}")
    private String adminEmail;

    public ApplicationInitializer(PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        Optional<User> admin = userRepository.findUserByEmail(adminEmail);

        if (admin.isEmpty()) {
            User initAdmin = new User()
                    .setEmail(adminEmail)
                    .setPassword(passwordEncoder.encode(adminPassword))
                    .setRole(UserRole.ADMIN)
                    .setStatus(UserStatus.ACTIVE);

            userRepository.save(initAdmin);
        }
    }
}