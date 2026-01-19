package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.Trim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TrimRepository extends JpaRepository<Trim, Long> {
    List<Trim> findByModelIdOrderByNameAsc(Long modelId);

    Optional<Trim> findByNameIgnoreCaseAndModelId(String trimName, Long id);
    Optional<Trim> findByNameAndModelId(String name, Long modelId);
    Optional<Trim> findByModelIdAndNameIgnoreCase(Long modelId, String name);
}

