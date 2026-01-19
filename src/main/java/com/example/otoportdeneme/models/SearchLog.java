package com.example.otoportdeneme.models;

import com.example.otoportdeneme.Enums.SearchLogType;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "search_logs", indexes = {
        @Index(name = "ix_searchlog_type", columnList = "type"),
        @Index(name = "ix_searchlog_client", columnList = "client_id"),
        @Index(name = "ix_searchlog_created", columnList = "createdAt")
})
public class SearchLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Login olmuş kullanıcı varsa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SearchLogType type;

    // Arama/filtre kriterlerinin JSON hali
    @Column(nullable = false, columnDefinition = "TEXT")
    private String criteriaJson;

    // Sonuç sayısı (analytics için güzel)
    private Integer resultCount;

    // Basit izleme bilgileri (opsiyonel)
    @Column(length = 45)
    private String ipAddress;      // IPv4/IPv6 string

    @Column(length = 255)
    private String userAgent;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public SearchLog() {}

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public SearchLogType getType() { return type; }
    public void setType(SearchLogType type) { this.type = type; }

    public String getCriteriaJson() { return criteriaJson; }
    public void setCriteriaJson(String criteriaJson) { this.criteriaJson = criteriaJson; }

    public Integer getResultCount() { return resultCount; }
    public void setResultCount(Integer resultCount) { this.resultCount = resultCount; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
