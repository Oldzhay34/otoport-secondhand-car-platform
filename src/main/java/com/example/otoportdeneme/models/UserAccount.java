package com.example.otoportdeneme.models;

import com.example.otoportdeneme.Enums.AccountStatus;
import com.example.otoportdeneme.Enums.ActorType;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 190)
    private String email;

    @Column(nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private AccountStatus status = AccountStatus.ACTIVE;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Transient
    public abstract ActorType getActorType();

    // ===== getters =====
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public AccountStatus getStatus() { return status; }
    public Instant getCreatedAt() { return createdAt; }

    // ===== setters =====
    public void setEmail(String email) { this.email = email; }

    // ✅ doğru setter
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }

    public void setStatus(AccountStatus status) { this.status = status; }

    public boolean isActive() { return status == AccountStatus.ACTIVE; }
}
