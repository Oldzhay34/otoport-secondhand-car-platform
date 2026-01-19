package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.Car;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Long> {
    List<Car> findByTrimId(Long trimId);
}

