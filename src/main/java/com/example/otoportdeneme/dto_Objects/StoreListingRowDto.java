package com.example.otoportdeneme.dto_Objects;

import java.math.BigDecimal;
import java.time.Instant;

public class StoreListingRowDto {
    private Long id;
    private String title;
    private String coverImageUrl;
    private String status;
    private BigDecimal price;
    private String currency;
    private Instant createdAt;

    public StoreListingRowDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCoverImageUrl() { return coverImageUrl; }
    public void setCoverImageUrl(String coverImageUrl) { this.coverImageUrl = coverImageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getPrice() { return price; }              // ✅
    public void setPrice(BigDecimal price) { this.price = price; } // ✅

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
