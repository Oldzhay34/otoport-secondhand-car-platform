package com.example.otoportdeneme.models;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class CarFeatureId implements Serializable {

    private Long carId;
    private Long featureId;

    public CarFeatureId() {}

    public CarFeatureId(Long carId, Long featureId) {
        this.carId = carId;
        this.featureId = featureId;
    }

    public Long getCarId() { return carId; }
    public void setCarId(Long carId) { this.carId = carId; }

    public Long getFeatureId() { return featureId; }
    public void setFeatureId(Long featureId) { this.featureId = featureId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CarFeatureId)) return false;
        CarFeatureId that = (CarFeatureId) o;
        return Objects.equals(carId, that.carId) && Objects.equals(featureId, that.featureId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(carId, featureId);
    }
}
