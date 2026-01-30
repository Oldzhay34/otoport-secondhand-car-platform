package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {

    Optional<Brand> findByNameIgnoreCase(String name);

    Optional<Brand> findByNameKey(String nameKey);

    @Modifying
    @Transactional
    @Query(value = """
  INSERT INTO brands(name, name_key) VALUES (?1, ?2)
  ON DUPLICATE KEY UPDATE name = VALUES(name)
""", nativeQuery = true)
    int upsertBrand(String name, String nameKey);

}







