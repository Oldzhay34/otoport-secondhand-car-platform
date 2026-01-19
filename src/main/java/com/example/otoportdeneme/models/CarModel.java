package com.example.otoportdeneme.models;

import jakarta.persistence.*;

@Entity
@Table(name = "car_models",
        indexes = {
                @Index(name = "ix_model_brand", columnList = "brand_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "ux_model_brand_name", columnNames = {"brand_id", "name"})
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

    public CarModel() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Brand getBrand() { return brand; }
    public void setBrand(Brand brand) { this.brand = brand; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
