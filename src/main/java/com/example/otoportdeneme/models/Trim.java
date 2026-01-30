package com.example.otoportdeneme.models;

import com.example.otoportdeneme.services.CatalogKey;
import jakarta.persistence.*;

@Entity
@Table(
        name = "trims",
        indexes = {
                @Index(name = "ix_trim_model", columnList = "model_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "ux_trim_model_name_key",
                        columnNames = {"model_id", "name_key"}
                )
        }
)
public class Trim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "model_id", nullable = false)
    private CarModel model;

    @Column(nullable = false, length = 120)
    private String name;

    @Column(name = "name_key", nullable = false, length = 180)
    private String nameKey;

    public Trim() {}

    @PrePersist
    @PreUpdate
    void syncKey() {
        this.nameKey = CatalogKey.keyOf(this.name);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CarModel getModel() { return model; }
    public void setModel(CarModel model) { this.model = model; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getNameKey() { return nameKey; }
}
