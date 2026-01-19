package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.Interfaces.ClientProfileView;
import com.example.otoportdeneme.dto_Objects.ClientProfileDto;
import com.example.otoportdeneme.dto_Requests.ClientProfileUpdateRequest;
import com.example.otoportdeneme.dto_Response.ClientProfileGetResponse;
import com.example.otoportdeneme.dto_Response.ClientProfileResponse;
import com.example.otoportdeneme.repositories.UserAccountRepository;
import com.example.otoportdeneme.services.ClientProfileService;
import com.example.otoportdeneme.services.ClientProfileUpdateService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/profile")
public class ClientProfileController {

    private final ClientProfileService profileService;
    private final ClientProfileUpdateService updateService;
    private final UserAccountRepository userAccountRepository;

    public ClientProfileController(
            ClientProfileService profileService,
            ClientProfileUpdateService updateService,
            UserAccountRepository userAccountRepository
    ) {
        this.profileService = profileService;
        this.updateService = updateService;
        this.userAccountRepository = userAccountRepository;
    }

    @GetMapping("/me")
    public ClientProfileGetResponse me(Authentication auth, HttpServletRequest req) {

        Long clientId = resolveUserId(auth);

        var opt = profileService.getMyProfile(
                clientId,
                req.getRemoteAddr(),
                req.getHeader("User-Agent")
        );

        if (opt.isEmpty()) {
            return new ClientProfileGetResponse(false, null);
        }

        return new ClientProfileGetResponse(true, toDto(opt.get()));
    }

    @PutMapping("/me")
    public ClientProfileResponse updateMe(
            Authentication auth,
            @Valid @RequestBody ClientProfileUpdateRequest body
    ) {
        Long clientId = resolveUserId(auth);

        ClientProfileView updated = updateService.updateMyProfile(
                clientId,
                body.getFirstName(),
                body.getLastName(),
                body.getPhone(),
                body.getBirthDate(),
                body.getMarketingConsent()
        );

        return new ClientProfileResponse(toDto(updated));
    }

    private Long resolveUserId(Authentication auth) {
        if (auth == null) return null;

        String email = auth.getName(); // CustomUserDetailsService -> username = email
        return userAccountRepository.findIdByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    private ClientProfileDto toDto(ClientProfileView v) {
        ClientProfileDto dto = new ClientProfileDto();
        dto.setId(v.getId());
        dto.setFirstName(v.getFirstName());
        dto.setLastName(v.getLastName());
        dto.setEmail(v.getEmail());
        dto.setPhone(v.getPhone());
        dto.setBirthDate(v.getBirthDate());
        dto.setMarketingConsent(v.getMarketingConsent());
        return dto;
    }
}
