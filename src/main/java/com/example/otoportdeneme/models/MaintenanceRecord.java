package com.example.otoportdeneme.models;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "maintenance_records", indexes = {
        @Index(name = "ix_maint_car_date", columnList = "car_id,serviceDate"),
        @Index(name = "ix_maint_car_km", columnList = "car_id,kilometer")
})
public class MaintenanceRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    private LocalDate serviceDate;

    private Integer kilometer;

    @Column(length = 120)
    private String serviceName; // "Yetkili servis", "Özel servis" vs.

    @Column(length = 255)
    private String description; // "Yağ bakımı", "Triger değişimi" vs.

    public MaintenanceRecord() {}

    // getters/setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }

    public LocalDate getServiceDate() { return serviceDate; }
    public void setServiceDate(LocalDate serviceDate) { this.serviceDate = serviceDate; }

    public Integer getKilometer() { return kilometer; }
    public void setKilometer(Integer kilometer) { this.kilometer = kilometer; }

    public String getServiceName() { return serviceName; }
    public void setServiceName(String serviceName) { this.serviceName = serviceName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
