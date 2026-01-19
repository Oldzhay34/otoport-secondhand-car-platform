package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.Feature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FeatureRepository extends JpaRepository<Feature, Long> {
    Optional<Feature> findByCodeIgnoreCase(String code);
    boolean existsByCodeIgnoreCase(String code);
}

