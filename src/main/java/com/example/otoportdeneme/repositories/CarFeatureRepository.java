package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.CarFeature;
import com.example.otoportdeneme.models.CarFeatureId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarFeatureRepository extends JpaRepository<CarFeature, CarFeatureId> {
    List<CarFeature> findByCarId(Long carId);
    boolean existsByCarIdAndFeatureId(Long carId, Long featureId);
    void deleteByCarIdAndFeatureId(Long carId, Long featureId);
}
