package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.Trim;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TrimRepository extends JpaRepository<Trim, Long> {
    List<Trim> findByModelIdOrderByNameAsc(Long modelId);

    Optional<Trim> findByNameIgnoreCaseAndModelId(String trimName, Long id);
    Optional<Trim> findByNameAndModelId(String name, Long modelId);
    Optional<Trim> findByModelIdAndNameKey(Long modelId, String nameKey);

    @Modifying
    @Transactional
    @Query(value = """
        INSERT INTO trims(model_id, name, name_key)
        VALUES (?1, ?2, ?3)
        ON DUPLICATE KEY UPDATE name = VALUES(name)
    """, nativeQuery = true)
    void upsertTrim(Long modelId, String name, String nameKey);


}

