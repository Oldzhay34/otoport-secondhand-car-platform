package com.example.otoportdeneme.models;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "request_logs", indexes = {
        @Index(name = "ix_req_created", columnList = "createdAt"),
        @Index(name = "ix_req_guest_created", columnList = "isGuest,createdAt"),
        @Index(name = "ix_req_user_created", columnList = "userId,createdAt"),
        @Index(name = "ix_req_path_created", columnList = "path,createdAt")
})
public class RequestLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // USER yoksa guest
    @Column(nullable = false)
    private Boolean isGuest = true;

    // USER varsa set edilir (client/store/admin)
    private Long userId;

    @Column(length = 20)
    private String role; // CLIENT/STORE/ADMIN

    @Column(nullable = false, length = 255)
    private String path; // /api/home..., /api/listings/.. vs

    @Column(length = 10)
    private String method;

    @Column(length = 45)
    private String ipAddress;

    @Column(length = 255)
    private String userAgent;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public RequestLog() {}

    public Long getId() { return id; }
    public Boolean getIsGuest() { return isGuest; }
    public void setIsGuest(Boolean guest) { isGuest = guest; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public Instant getCreatedAt() { return createdAt; }
}
