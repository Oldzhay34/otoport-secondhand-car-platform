package com.example.otoportdeneme.services;

import com.example.otoportdeneme.models.Store;
import com.example.otoportdeneme.repositories.StoreRepository;
import com.example.otoportdeneme.repositories.UserAccountRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class StoreAdminServiceImpl implements StoreAdminService {

    private final StoreRepository storeRepository;
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    public StoreAdminServiceImpl(StoreRepository storeRepository,
                                 UserAccountRepository userAccountRepository,
                                 PasswordEncoder passwordEncoder) {
        this.storeRepository = storeRepository;
        this.userAccountRepository = userAccountRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Store createStoreWithPassword(String email,
                                         String rawPassword,
                                         String storeName,
                                         String city,
                                         String district,
                                         String phone) {

        if (userAccountRepository.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Bu email zaten kayıtlı.");
        }

        Store s = new Store();
        s.setEmail(email);

        // ✅ users.passwordHash dolacak
        s.setPasswordHash(passwordEncoder.encode(rawPassword));

        s.setStoreName(storeName);
        s.setCity(city);
        s.setDistrict(district);
        s.setPhone(phone);

        return storeRepository.save(s);
    }
}
