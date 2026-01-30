package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.WalEntry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface WalEntryRepository extends JpaRepository<WalEntry, Long>, JpaSpecificationExecutor<WalEntry> {

    List<WalEntry> findAllByOrderByCreatedAtDesc(Pageable pageable);
    List<WalEntry> findAllByOrderByCreatedAtAsc(Pageable pageable);

    WalEntry findTopByOrderByIdDesc(); // son zincir hash için
}
