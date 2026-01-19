package com.example.otoportdeneme.models;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "car_features", indexes = {
        @Index(name = "ix_car_feature_car", columnList = "car_id"),
        @Index(name = "ix_car_feature_feature", columnList = "feature_id")
})
public class CarFeature {

    @EmbeddedId
    private CarFeatureId id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("carId")
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @MapsId("featureId")
    @JoinColumn(name = "feature_id", nullable = false)
    private Feature feature;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public CarFeature() {}

    public CarFeature(Car car, Feature feature) {
        this.car = car;
        this.feature = feature;
        this.id = new CarFeatureId(car.getId(), feature.getId());
    }

    public CarFeatureId getId() { return id; }
    public void setId(CarFeatureId id) { this.id = id; }

    public Car getCar() { return car; }
    public void setCar(Car car) { this.car = car; }

    public Feature getFeature() { return feature; }
    public void setFeature(Feature feature) { this.feature = feature; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
