package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Requests.StoreChangePasswordRequest;
import com.example.otoportdeneme.models.Store;
import com.example.otoportdeneme.repositories.StoreRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.*;
import java.util.Locale;
import java.util.Set;

@Service
public class StoreAccountServiceImpl implements StoreAccountService {

    private final StoreRepository storeRepository;
    private final PasswordEncoder passwordEncoder;

    // ✅ statik servis edilen uploads klasörü
    private final Path uploadRoot = Paths.get("src/main/resources/static/uploads");

    public StoreAccountServiceImpl(StoreRepository storeRepository,
                                   PasswordEncoder passwordEncoder) {
        this.storeRepository = storeRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void changePassword(Long storeId, StoreChangePasswordRequest req) {
        if (req == null) throw new IllegalArgumentException("Request required");

        String oldPw = safe(req.getOldPassword());
        String newPw = safe(req.getNewPassword());
        String confirm = safe(req.getConfirmPassword());

        if (oldPw.isEmpty() || newPw.isEmpty() || confirm.isEmpty()) {
            throw new IllegalArgumentException("All fields are required");
        }
        if (!newPw.equals(confirm)) {
            throw new IllegalArgumentException("New passwords do not match");
        }
        if (newPw.length() < 8) {
            throw new IllegalArgumentException("Password must be at least 8 characters");
        }

        Store s = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        // ✅ doğru alan
        String currentHash = s.getPasswordHash();
        if (currentHash == null || !passwordEncoder.matches(oldPw, currentHash)) {
            throw new IllegalArgumentException("Old password is incorrect");
        }

        // ✅ doğru setter
        s.setPasswordHash(passwordEncoder.encode(newPw));
        storeRepository.save(s);
    }

    @Override
    @Transactional
    public String updateLogo(Long storeId, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is required");
        }

        String ct = file.getContentType() == null ? "" : file.getContentType().toLowerCase(Locale.ROOT);
        Set<String> allowed = Set.of("image/png", "image/jpeg", "image/jpg", "image/webp");
        if (!allowed.contains(ct)) {
            throw new IllegalArgumentException("Only PNG/JPEG/WEBP allowed");
        }

        String ext = ".png";
        if (ct.contains("jpeg") || ct.contains("jpg")) ext = ".jpg";
        if (ct.contains("webp")) ext = ".webp";

        Store s = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        try {
            // ✅ /static/uploads/stores/{id}/logo.ext
            Path dir = uploadRoot.resolve("stores").resolve(String.valueOf(storeId));
            Files.createDirectories(dir);

            Path target = dir.resolve("logo" + ext);
            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            // ✅ browser URL (SecurityConfig permitAll: /uploads/**)
            String url = "/uploads/stores/" + storeId + "/logo" + ext;
            s.setLogoUrl(url);
            storeRepository.save(s);

            return url;

        } catch (Exception e) {
            throw new IllegalStateException("Logo upload failed", e);
        }
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
