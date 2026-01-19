package com.example.otoportdeneme.models;

import jakarta.persistence.*;

@Entity
@Table(name = "trims",
        indexes = {
                @Index(name = "ix_trim_model", columnList = "model_id")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "ux_trim_model_name", columnNames = {"model_id", "name"})
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
    private String name; // Örn: "Dream", "Elegance", "AMG"

    public Trim() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CarModel getModel() { return model; }
    public void setModel(CarModel model) { this.model = model; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
