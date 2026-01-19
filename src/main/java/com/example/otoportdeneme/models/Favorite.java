package com.example.otoportdeneme.models;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "favorites", indexes = {
        @Index(name = "ix_fav_client", columnList = "client_id"),
        @Index(name = "ix_fav_listing", columnList = "listing_id"),
        @Index(name = "ix_fav_created", columnList = "createdAt")
})
public class Favorite {

    @EmbeddedId
    private FavoriteId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("clientId")
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("listingId")
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Favorite() {}

    public Favorite(Client client, Listing listing) {
        this.client = client;
        this.listing = listing;
        this.id = new FavoriteId(client.getId(), listing.getId());
    }

    public FavoriteId getId() { return id; }
    public void setId(FavoriteId id) { this.id = id; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Listing getListing() { return listing; }
    public void setListing(Listing listing) { this.listing = listing; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
