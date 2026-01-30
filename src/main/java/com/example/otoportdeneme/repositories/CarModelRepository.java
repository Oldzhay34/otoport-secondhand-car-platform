package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CarModelRepository extends JpaRepository<CarModel, Long> {
    List<CarModel> findByBrandIdOrderByNameAsc(Long brandId);

    Optional<CarModel> findByNameIgnoreCaseAndBrandId(String a3, Long id);

    Optional<CarModel> findByBrandIdAndNameIgnoreCase(Long id, String trim);

    Optional<CarModel> findByBrandIdAndNameKey(Long brandId, String nameKey);

    @Query(value = """
  INSERT INTO car_models(brand_id, name, name_key)
  VALUES (?1, ?2, ?3)
  ON DUPLICATE KEY UPDATE name = VALUES(name)
""", nativeQuery = true)
    @Modifying @Transactional
    void upsertModel(Long brandId, String name, String nameKey);

}
