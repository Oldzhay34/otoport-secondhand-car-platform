package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Objects.ExpertReportDto;
import com.example.otoportdeneme.services.ExpertReportService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

// Listing entity/repo sende hangi isimdeyse onu import et
import com.example.otoportdeneme.models.Listing;
import com.example.otoportdeneme.repositories.ListingRepository;

@RestController
@RequestMapping("/api")
public class ExpertReportController {

    private final ExpertReportService expertReportService;
    private final ListingRepository listingRepository;

    public ExpertReportController(ExpertReportService expertReportService,
                                  ListingRepository listingRepository) {
        this.expertReportService = expertReportService;
        this.listingRepository = listingRepository;
    }

    @GetMapping("/listings/{listingId}/expert-report")
    public ExpertReportDto getByListingId(@PathVariable Long listingId) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "İlan bulunamadı."));

        if (listing.getCar() == null || listing.getCar().getId() == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "İlanın aracı bulunamadı.");
        }

        return expertReportService.getByCarId(listing.getCar().getId());
    }

    @GetMapping("/cars/{carId}/expert-report")
    public ExpertReportDto getByCarId(@PathVariable Long carId) {
        return expertReportService.getByCarId(carId);
    }
}
