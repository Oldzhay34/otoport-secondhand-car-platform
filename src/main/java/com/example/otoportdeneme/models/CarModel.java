package com.example.otoportdeneme.models;

import com.example.otoportdeneme.services.CatalogKey;
import jakarta.persistence.*;

@Entity
@Table(
        name = "car_models",
        indexes = {
                @Index(name = "ix_model_brand", columnList = "brand_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "ux_model_brand_name_key",
                        columnNames = {"brand_id", "name_key"}
                )
        }
)
public class CarModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "brand_id", nullable = false)
    private Brand brand;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "name_key", nullable = false, length = 150)
    private String nameKey;

    public CarModel() {}

    @PrePersist
    @PreUpdate
    void syncKey() {
        this.nameKey = CatalogKey.keyOf(this.name);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Brand getBrand() { return brand; }
    public void setBrand(Brand brand) { this.brand = brand; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNameKey() { return nameKey; }
}
