package com.example.otoportdeneme.models;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "store_subscriptions", indexes = {
        @Index(name = "ux_store_sub_store", columnList = "store_id", unique = true),
        @Index(name = "ix_store_sub_active", columnList = "isActive,endsAt")
})
public class StoreSubscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false, unique = true)
    private Store store;

    @Column(nullable = false)
    private Boolean isActive = true;

    private Instant startsAt;
    private Instant endsAt;

    // tek tip paket olduğu için: sadece limitler
    @Column(nullable = false)
    private Integer listingLimit = 50;

    @Column(nullable = false)
    private Integer featuredLimit = 0;

    public StoreSubscription() {}

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Store getStore() { return store; }
    public void setStore(Store store) { this.store = store; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }

    public Instant getStartsAt() { return startsAt; }
    public void setStartsAt(Instant startsAt) { this.startsAt = startsAt; }

    public Instant getEndsAt() { return endsAt; }
    public void setEndsAt(Instant endsAt) { this.endsAt = endsAt; }

    public Integer getListingLimit() { return listingLimit; }
    public void setListingLimit(Integer listingLimit) { this.listingLimit = listingLimit; }

    public Integer getFeaturedLimit() { return featuredLimit; }
    public void setFeaturedLimit(Integer featuredLimit) { this.featuredLimit = featuredLimit; }
}
