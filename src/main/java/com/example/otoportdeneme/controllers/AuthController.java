package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.Enums.ActorType;
import com.example.otoportdeneme.Enums.AuditAction;
import com.example.otoportdeneme.dto_Requests.LoginRequest;
import com.example.otoportdeneme.dto_Requests.RegisterRequest;
import com.example.otoportdeneme.models.Client;
import com.example.otoportdeneme.models.UserAccount;
import com.example.otoportdeneme.repositories.ClientRepository;
import com.example.otoportdeneme.repositories.UserAccountRepository;
import com.example.otoportdeneme.services.AuditService;
import com.example.otoportdeneme.services.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    private final UserAccountRepository userAccountRepository;
    private final ClientRepository clientRepository;

    private final AuditService auditService;

    public AuthController(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            PasswordEncoder passwordEncoder,
            UserAccountRepository userAccountRepository,
            ClientRepository clientRepository,
            AuditService auditService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.userAccountRepository = userAccountRepository;
        this.clientRepository = clientRepository;
        this.auditService = auditService;
    }

    @PostMapping("/login")
    public Map<String, Object> login(@Valid @RequestBody LoginRequest req,
                                     HttpServletRequest httpReq) {

        String email = normalizeEmail(req.getEmail());

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, req.getPassword())
        );

        UserAccount ua = userAccountRepository.findByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Kullanıcı bulunamadı."));

        ActorType actorType = ua.getActorType();
        String role = (actorType != null) ? actorType.name() : ActorType.CLIENT.name();

        // ✅ LOGIN audit bas
        auditService.log(
                actorType, ua.getId(),
                AuditAction.LOGIN,
                "AUTH", null,
                "{\"note\":\"login\"}",
                resolveClientIp(httpReq),
                httpReq.getHeader("User-Agent")
        );

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("id", ua.getId());

        String token = jwtService.generateToken(email, claims);

        return Map.of("token", token);
    }

    @PostMapping("/register")
    public Map<String, Object> register(@Valid @RequestBody RegisterRequest req,
                                        HttpServletRequest httpReq) {

        String email = normalizeEmail(req.getEmail());

        if (userAccountRepository.findByEmail(email).isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Bu email zaten kayıtlı.");
        }

        // ✅ sadece CLIENT register
        Client c = new Client();
        c.setEmail(email);
        c.setPasswordHash(passwordEncoder.encode(req.getPassword())); // ✅ doğru setter

        c.setFirstName(req.getFirstName());
        c.setLastName(req.getLastName());
        c.setPhone(req.getPhone());
        c.setMarketingConsent(Boolean.TRUE.equals(req.getMarketingConsent()));

        clientRepository.save(c);

        // opsiyonel: register sonrası login (authenticate)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(email, req.getPassword())
        );

        // ✅ REGISTER sonrası da LOGIN gibi sayılacaksa audit bas
        auditService.log(
                ActorType.CLIENT, c.getId(),
                AuditAction.LOGIN,
                "AUTH", null,
                "{\"note\":\"register+login\"}",
                resolveClientIp(httpReq),
                httpReq.getHeader("User-Agent")
        );

        Map<String, Object> claims = new HashMap<>();
        claims.put("role", ActorType.CLIENT.name());
        claims.put("id", c.getId());

        String token = jwtService.generateToken(email, claims);

        return Map.of("token", token);
    }

    private String normalizeEmail(String email) {
        if (email == null) return "";
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private String resolveClientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
        String xri = req.getHeader("X-Real-IP");
        if (xri != null && !xri.isBlank()) return xri.trim();
        return req.getRemoteAddr();
    }
}
