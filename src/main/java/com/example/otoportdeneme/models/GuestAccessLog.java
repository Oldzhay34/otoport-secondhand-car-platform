package com.example.otoportdeneme.models;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(
        name = "guest_access_logs",
        indexes = {
                @Index(name = "ix_guest_access_type", columnList = "access_type, created_at"),
                @Index(name = "ix_guest_created_at", columnList = "created_at")
        }
)
public class GuestAccessLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "access_type", nullable = false, length = 50)
    private String accessType;

    @Column(name = "target", length = 255)
    private String target;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public GuestAccessLog() {}

    public Long getId() { return id; }

    public String getAccessType() { return accessType; }
    public void setAccessType(String accessType) { this.accessType = accessType; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public Instant getCreatedAt() { return createdAt; }
}
