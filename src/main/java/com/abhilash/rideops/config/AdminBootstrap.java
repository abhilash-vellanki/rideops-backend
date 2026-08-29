package com.abhilash.rideops.config;

import com.abhilash.rideops.entities.User;
import com.abhilash.rideops.entities.enums.Role;
import com.abhilash.rideops.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminBootstrap implements ApplicationRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.bootstrap.admin.email:}")
    private String adminEmail;
    @Value("${app.bootstrap.admin.password:}")
    private String adminPassword;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (adminEmail.isBlank() && adminPassword.isBlank()) {
            return;
        }
        if (adminEmail.isBlank() || adminPassword.length() < 8 || adminPassword.length() > 72) {
            throw new IllegalStateException(
                    "ADMIN_EMAIL and an ADMIN_PASSWORD between 8 and 72 characters must be configured together");
        }

        String normalizedEmail = adminEmail.trim().toLowerCase();
        User admin = userRepository.findByEmail(normalizedEmail).orElseGet(() -> {
            User user = new User();
            user.setName("RideOps Administrator");
            user.setEmail(normalizedEmail);
            user.setPassword(passwordEncoder.encode(adminPassword));
            user.setRoles(new HashSet<>());
            return user;
        });
        Set<Role> roles = admin.getRoles() == null ? new HashSet<>() : new HashSet<>(admin.getRoles());
        if (roles.add(Role.ADMIN)) {
            admin.setRoles(roles);
            userRepository.save(admin);
            log.info("Configured bootstrap administrator account");
        }
    }
}
