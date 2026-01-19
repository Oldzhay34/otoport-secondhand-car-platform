package com.example.otoportdeneme.models;

import com.example.otoportdeneme.Enums.ReportStatus;
import com.example.otoportdeneme.Enums.ReportReason;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "listing_reports", indexes = {
        @Index(name = "ix_report_listing", columnList = "listing_id,createdAt"),
        @Index(name = "ix_report_status", columnList = "status,createdAt"),
        @Index(name = "ix_report_client", columnList = "client_id,createdAt")
})
public class ListingReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Şikayet edilen ilan
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    // Login olmuşsa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    // Login yoksa
    @Column(length = 80)
    private String guestName;

    @Column(length = 190)
    private String guestEmail;

    @Column(length = 30)
    private String guestPhone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ReportReason reason;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ReportStatus status = ReportStatus.OPEN;

    // Admin moderasyon notu
    @Column(length = 1000)
    private String adminNote;

    // Kapatma zamanı
    private Instant closedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public ListingReport() {}

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Listing getListing() { return listing; }
    public void setListing(Listing listing) { this.listing = listing; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public String getGuestEmail() { return guestEmail; }
    public void setGuestEmail(String guestEmail) { this.guestEmail = guestEmail; }

    public String getGuestPhone() { return guestPhone; }
    public void setGuestPhone(String guestPhone) { this.guestPhone = guestPhone; }

    public ReportReason getReason() { return reason; }
    public void setReason(ReportReason reason) { this.reason = reason; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public ReportStatus getStatus() { return status; }
    public void setStatus(ReportStatus status) { this.status = status; }

    public String getAdminNote() { return adminNote; }
    public void setAdminNote(String adminNote) { this.adminNote = adminNote; }

    public Instant getClosedAt() { return closedAt; }
    public void setClosedAt(Instant closedAt) { this.closedAt = closedAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
