package com.example.otoportdeneme.jobs;

import com.example.otoportdeneme.services.archive.InquiryArchiveService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Component
public class InquiryArchiveJob {

    private final InquiryArchiveService archiveService;

    public InquiryArchiveJob(InquiryArchiveService archiveService) {
        this.archiveService = archiveService;
    }

    // Her gece 03:30 çalışsın
    @Scheduled(cron = "0 30 3 * * *")
    public void run() {
        Instant before = Instant.now().minus(30, ChronoUnit.DAYS); // 30 günden eski
        archiveService.archiveOlderThan(before, 200);
    }
}
