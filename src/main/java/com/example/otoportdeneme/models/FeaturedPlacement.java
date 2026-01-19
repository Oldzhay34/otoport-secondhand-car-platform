package com.example.otoportdeneme.models;

import com.example.otoportdeneme.Enums.PlacementType;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "featured_placements",
        indexes = {
                @Index(name = "ix_fp_type_active", columnList = "type,isActive,startAt,endAt,priority"),
                @Index(name = "ix_fp_listing", columnList = "listing_id"),
                @Index(name = "ix_fp_campaign", columnList = "campaign_id")
        },
        uniqueConstraints = {
                // Aynı listing aynı type içinde aynı anda 2 kez aktif olmasın
                @UniqueConstraint(name = "ux_fp_listing_type", columnNames = {"listing_id", "type"})
        }
)
public class FeaturedPlacement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Hangi kampanya altında?
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "campaign_id", nullable = false)
    private AdCampaign campaign;

    // Hangi ilan öne çıkarılıyor?
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "listing_id", nullable = false)
    private Listing listing;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PlacementType type = PlacementType.HOME_TOP;

    // Aktif/pasif
    @Column(nullable = false)
    private Boolean isActive = true;

    // Tarih aralığı (kampanyadan bağımsız override edebilirsin)
    @Column(nullable = false)
    private Instant startAt;

    @Column(nullable = false)
    private Instant endAt;

    // ✅ En üst sıralama: yüksek priority önce gelir
    @Column(nullable = false)
    private Integer priority = 100;

    // “reklam alanı” içinde manuel sıralama gerekirse
    @Column(nullable = false)
    private Integer sortOrder = 0;

    // Analytics sayaçları (basit)
    @Column(nullable = false)
    private Long impressionCount = 0L;

    @Column(nullable = false)
    private Long clickCount = 0L;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public FeaturedPlacement() {}

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AdCampaign getCampaign() { return campaign; }
    public void setCampaign(AdCampaign campaign) { this.campaign = campaign; }

    public Listing getListing() { return listing; }
    public void setListing(Listing listing) { this.listing = listing; }

    public PlacementType getType() { return type; }
    public void setType(PlacementType type) { this.type = type; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }

    public Instant getStartAt() { return startAt; }
    public void setStartAt(Instant startAt) { this.startAt = startAt; }

    public Instant getEndAt() { return endAt; }
    public void setEndAt(Instant endAt) { this.endAt = endAt; }

    public Integer getPriority() { return priority; }
    public void setPriority(Integer priority) { this.priority = priority; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Long getImpressionCount() { return impressionCount; }
    public void setImpressionCount(Long impressionCount) { this.impressionCount = impressionCount; }

    public Long getClickCount() { return clickCount; }
    public void setClickCount(Long clickCount) { this.clickCount = clickCount; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
