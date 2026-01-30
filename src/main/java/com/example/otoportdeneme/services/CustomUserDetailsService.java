package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.AccountStatus;
import com.example.otoportdeneme.Interfaces.AppUserDetailsService;
import com.example.otoportdeneme.models.UserAccount;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import com.example.otoportdeneme.repositories.UserAccountRepository;

import java.util.List;

@Service
public class CustomUserDetailsService implements AppUserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public CustomUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserAccount ua = loadDomainUserByEmail(email);

        if (ua.getStatus() != AccountStatus.ACTIVE) {
            throw new DisabledException("Account is not active");
        }

        String role = "ROLE_" + ua.getActorType().name(); // ADMIN/STORE/CLIENT

        System.out.println("CDS email=" + ua.getEmail()
                + " actorType=" + ua.getActorType()
                + " status=" + ua.getStatus());

        return User.withUsername(ua.getEmail())
                .password(ua.getPasswordHash())
                .authorities(role)
                .build();



    }

    @Override
    public UserAccount loadDomainUserByEmail(String email) throws UsernameNotFoundException {
        return userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
