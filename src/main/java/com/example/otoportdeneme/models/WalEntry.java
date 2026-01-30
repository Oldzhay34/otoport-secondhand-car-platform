package com.example.otoportdeneme.models;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "wal_entries",
        indexes = {
                @Index(name = "ix_wal_created_at", columnList = "createdAt"),
                @Index(name = "ix_wal_actor", columnList = "actorType,actorId"),
                @Index(name = "ix_wal_path", columnList = "path")
        }
)
public class WalEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    // Actor (admin)
    @Column(length = 32)
    private String actorType; // "ADMIN"
    private Long actorId;

    // Request meta
    @Column(length = 16)
    private String method;

    @Column(length = 512)
    private String path;

    @Column(length = 2048)
    private String queryString;

    private Integer status;

    @Column(length = 64)
    private String ipAddress;

    @Column(length = 512)
    private String userAgent;

    // (Opsiyonel) body snapshot
    @Lob
    @Column(columnDefinition = "TEXT")
    private String requestBody;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String responseBody;

    // Basit “tamper-evident” zincir (opsiyonel ama güzel)
    @Column(length = 88)
    private String prevHash;

    @Column(length = 88)
    private String hash;

    // ---- getters/setters ----

    public Long getId() { return id; }
    public Instant getCreatedAt() { return createdAt; }

    public String getActorType() { return actorType; }
    public void setActorType(String actorType) { this.actorType = actorType; }

    public Long getActorId() { return actorId; }
    public void setActorId(Long actorId) { this.actorId = actorId; }

    public String getMethod() { return method; }
    public void setMethod(String method) { this.method = method; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public String getQueryString() { return queryString; }
    public void setQueryString(String queryString) { this.queryString = queryString; }

    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getRequestBody() { return requestBody; }
    public void setRequestBody(String requestBody) { this.requestBody = requestBody; }

    public String getResponseBody() { return responseBody; }
    public void setResponseBody(String responseBody) { this.responseBody = responseBody; }

    public String getPrevHash() { return prevHash; }
    public void setPrevHash(String prevHash) { this.prevHash = prevHash; }

    public String getHash() { return hash; }
    public void setHash(String hash) { this.hash = hash; }
}
