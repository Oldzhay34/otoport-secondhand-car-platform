package com.example.otoportdeneme.models;

import com.example.otoportdeneme.Enums.InquiryStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "inquiries", indexes = {
        @Index(name = "ix_inquiry_store", columnList = "store_id"),
        @Index(name = "ix_inquiry_listing", columnList = "listing_id"),
        @Index(name = "ix_inquiry_client", columnList = "client_id"),
        @Index(name = "ix_inquiry_created", columnList = "createdAt")
})
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Hangi ilan için?
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    // Hangi mağazaya gidiyor?
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "store_id", nullable = false)
    private Store store;

    // Login olmuş client varsa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    // Login yoksa “lead” bilgileri
    @Column(length = 80)
    private String guestName;

    @Column(length = 190)
    private String guestEmail;

    @Column(length = 30)
    private String guestPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InquiryStatus status = InquiryStatus.OPEN;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Inquiry() {}

    // -------- getters --------
    public Long getId() { return id; }

    public Listing getListing() { return listing; }

    public Store getStore() { return store; }

    public Client getClient() { return client; }

    public String getGuestName() { return guestName; }

    public String getGuestEmail() { return guestEmail; }

    public String getGuestPhone() { return guestPhone; }

    public InquiryStatus getStatus() { return status; }

    public Instant getCreatedAt() { return createdAt; }

    // -------- setters --------
    public void setId(Long id) { this.id = id; }

    public void setListing(Listing listing) { this.listing = listing; }

    public void setStore(Store store) { this.store = store; }

    public void setClient(Client client) { this.client = client; }

    public void setGuestName(String guestName) { this.guestName = guestName; }

    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }

    public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }

    public void setStatus(InquiryStatus status) { this.status = status; }

    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
