package com.example.otoportdeneme.models;

import jakarta.persistence.*;

@Entity
@Table(name = "features",
        indexes = { @Index(name = "ux_feature_code", columnList = "code", unique = true) }
)
public class Feature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ABS, ESP, SUNROOF gibi sabit kod
    @Column(nullable = false, length = 60, unique = true)
    private String code;

    // UI’da görünen ad (Türkçe)
    @Column(nullable = false, length = 120)
    private String displayName;

    public Feature() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDisplayName() { return displayName; }
    public void setDisplayName(String displayName) { this.displayName = displayName; }

    public String getName() {
        return displayName;
    }
}
