package com.example.otoportdeneme.repositories;


import com.example.otoportdeneme.Enums.ReportStatus;
import com.example.otoportdeneme.models.ListingReport;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingReportRepository extends JpaRepository<ListingReport, Long> {

    List<ListingReport> findByStatusOrderByCreatedAtDesc(ReportStatus status);

    List<ListingReport> findByListingIdOrderByCreatedAtDesc(Long listingId);

    List<ListingReport> findByClientIdOrderByCreatedAtDesc(Long clientId);
}
