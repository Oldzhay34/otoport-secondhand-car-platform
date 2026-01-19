package com.example.otoportdeneme.Interfaces;

import com.example.otoportdeneme.models.UserAccount;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Spring Security UserDetailsService + domain tarafında UserAccount'a erişim
 */
public interface AppUserDetailsService extends UserDetailsService {

    /**
     * Security dışında, business/service katmanlarında direkt domain user'ı almak için.
     */
    UserAccount loadDomainUserByEmail(String email);
}
