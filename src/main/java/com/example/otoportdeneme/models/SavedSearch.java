package com.example.otoportdeneme.models;

import com.example.otoportdeneme.Enums.SavedSearchType;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "saved_searches", indexes = {
        @Index(name = "ix_saved_search_client", columnList = "client_id"),
        @Index(name = "ix_saved_search_type", columnList = "type"),
        @Index(name = "ix_saved_search_created", columnList = "createdAt")
})
public class SavedSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Kaydeden müşteri
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SavedSearchType type;

    // Kullanıcı "İstanbul SUV" gibi isim verebilir
    @Column(nullable = false, length = 120)
    private String name;

    // DTO'nun JSON hali (StoreSearchRequest veya ListingFilterRequest)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String criteriaJson;

    // Bildirim ileride istersen
    @Column(nullable = false)
    private Boolean notificationsEnabled = false;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt;

    public SavedSearch() {}

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public SavedSearchType getType() { return type; }
    public void setType(SavedSearchType type) { this.type = type; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCriteriaJson() { return criteriaJson; }
    public void setCriteriaJson(String criteriaJson) { this.criteriaJson = criteriaJson; }

    public Boolean getNotificationsEnabled() { return notificationsEnabled; }
    public void setNotificationsEnabled(Boolean notificationsEnabled) { this.notificationsEnabled = notificationsEnabled; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
}
