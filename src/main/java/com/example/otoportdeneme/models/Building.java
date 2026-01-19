package com.example.otoportdeneme.models;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "buildings", indexes = {
        @Index(name = "ix_building_city", columnList = "city"),
        @Index(name = "ix_building_name", columnList = "name")
})
public class Building {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Örn: "Galericiler Sitesi - A Blok"
    @Column(nullable = false, length = 140)
    private String name;

    // Senin senaryoda default 8
    @Column(nullable = false)
    private Integer totalFloors = 8;

    @Column(nullable = false, length = 60)
    private String city;

    @Column(length = 60)
    private String district;

    @Column(length = 255)
    private String addressLine;

    private Double latitude;
    private Double longitude;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Building() {}

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Integer getTotalFloors() { return totalFloors; }
    public void setTotalFloors(Integer totalFloors) { this.totalFloors = totalFloors; }

    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }

    public String getDistrict() { return district; }
    public void setDistrict(String district) { this.district = district; }

    public String getAddressLine() { return addressLine; }
    public void setAddressLine(String addressLine) { this.addressLine = addressLine; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
