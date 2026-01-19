package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.Enums.SavedSearchType;
import com.example.otoportdeneme.models.SavedSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SavedSearchRepository extends JpaRepository<SavedSearch, Long> {

    List<SavedSearch> findByClientIdOrderByCreatedAtDesc(Long clientId);

    List<SavedSearch> findByClientIdAndTypeOrderByCreatedAtDesc(Long clientId, SavedSearchType type);
}

