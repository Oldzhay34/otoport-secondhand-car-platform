package com.example.otoportdeneme.models;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "listing_views")
public class ListingView {

    @EmbeddedId
    private ListingViewId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("userId")
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("listingId")
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public ListingView() {}

    public ListingView(UserAccount user, Listing listing) {
        this.user = user;
        this.listing = listing;
        this.id = new ListingViewId(user.getId(), listing.getId());
    }

    public ListingViewId getId() { return id; }
    public UserAccount getUser() { return user; }
    public Listing getListing() { return listing; }
    public Instant getCreatedAt() { return createdAt; }
}
