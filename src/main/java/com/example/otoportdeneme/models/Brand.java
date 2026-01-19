package com.example.otoportdeneme.models;

import jakarta.persistence.*;

@Entity
@Table(name = "brands", indexes = {
        @Index(name = "ux_brand_name", columnList = "name", unique = true)
})
public class Brand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80, unique = true)
    private String name;

    public Brand() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}
