package com.example.otoportdeneme.Configs;

import com.example.otoportdeneme.Enums.AccountStatus;
import com.example.otoportdeneme.models.Admin;
import com.example.otoportdeneme.repositories.UserAccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminSeed {

    @Bean
    public CommandLineRunner seedSuperAdmin(UserAccountRepository userAccountRepository,
                                            PasswordEncoder passwordEncoder) {
        return args -> {
            String email = "olcaykarahasan@admin.com";

            boolean exists = userAccountRepository.findByEmail(email).isPresent();
            if (exists) return;

            Admin a = new Admin();
            a.setEmail(email);
            a.setPasswordHash(passwordEncoder.encode("Admin12345*"));
            a.setStatus(AccountStatus.ACTIVE);
            a.setSuperAdmin(true);

            userAccountRepository.save(a);

            System.out.println("[SEED] SuperAdmin created: " + email);
        };
    }
}

