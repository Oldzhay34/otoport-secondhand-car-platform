package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CarModelRepository extends JpaRepository<CarModel, Long> {
    List<CarModel> findByBrandIdOrderByNameAsc(Long brandId);

    Optional<CarModel> findByNameIgnoreCaseAndBrandId(String a3, Long id);

    Optional<CarModel> findByBrandIdAndNameIgnoreCase(Long id, String trim);
}
