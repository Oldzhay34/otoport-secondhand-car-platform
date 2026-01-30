package com.example.otoportdeneme.models;

import com.example.otoportdeneme.services.CatalogKey;
import jakarta.persistence.*;

@Entity
@Table(
        name = "brands",
        uniqueConstraints = {
                @UniqueConstraint(name = "ux_brand_name_key", columnNames = "name_key")
        }
)
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    
    @Column(nullable = false, length = 80)
    private String name;

    // Normalize edilmiş anahtar (audi, bmw, mercedes-benz)
    @Column(name = "name_key", nullable = false, length = 100)
    private String nameKey;

    public Brand() {}

    @PrePersist
    @PreUpdate
    void syncKey() {
        this.nameKey = CatalogKey.keyOf(this.name);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNameKey() { return nameKey; }
}
