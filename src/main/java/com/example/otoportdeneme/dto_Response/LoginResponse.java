package com.example.otoportdeneme.dto_Response;

import com.example.otoportdeneme.Enums.ActorType;

public class LoginResponse {

    private String token;
    private String tokenType = "Bearer";
    private long expiresInMs;

    private Long userId;
    private String email;

    // ROLE_CLIENT / ROLE_STORE / ROLE_ADMIN gibi değil,
    // direkt CLIENT / STORE / ADMIN dönüyoruz (frontend için daha temiz)
    private ActorType actorType;

    // İstersen ayrıca "role" string olarak da dön (frontend basit kullanır)
    private String role;

    public LoginResponse() {}

    public LoginResponse(String token, long expiresInMs, Long userId, String email, ActorType actorType) {
        this.token = token;
        this.expiresInMs = expiresInMs;
        this.userId = userId;
        this.email = email;
        this.actorType = actorType;
        this.role = actorType != null ? actorType.name() : null;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public long getExpiresInMs() { return expiresInMs; }
    public void setExpiresInMs(long expiresInMs) { this.expiresInMs = expiresInMs; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public ActorType getActorType() { return actorType; }
    public void setActorType(ActorType actorType) {
        this.actorType = actorType;
        this.role = actorType != null ? actorType.name() : null;
    }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
