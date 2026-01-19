package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.InquiryMessageArchiveChunk;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface InquiryMessageArchiveChunkRepository extends JpaRepository<InquiryMessageArchiveChunk, Long> {

    List<InquiryMessageArchiveChunk> findByInquiryIdOrderByChunkNoAsc(Long inquiryId);

    Optional<InquiryMessageArchiveChunk> findByInquiryIdAndChunkNo(Long inquiryId, Integer chunkNo);

    boolean existsByInquiryIdAndChunkNo(Long inquiryId, Integer chunkNo);

    long countByInquiryId(Long inquiryId);
}
