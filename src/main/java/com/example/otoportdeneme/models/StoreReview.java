package com.example.otoportdeneme.models;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "store_reviews",
        indexes = {
                @Index(name = "ix_review_store", columnList = "store_id,createdAt"),
                @Index(name = "ix_review_client", columnList = "client_id,createdAt")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "ux_review_store_client", columnNames = {"store_id", "client_id"})
        }
)
public class StoreReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(nullable = false)
    private Integer rating; // 1..5 (validation service/DTO’da)

    @Column(length = 500)
    private String comment;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public StoreReview() {}

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Store getStore() { return store; }
    public void setStore(Store store) { this.store = store; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
