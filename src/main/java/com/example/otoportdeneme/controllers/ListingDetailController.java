package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Objects.ListingDetailDto;
import com.example.otoportdeneme.repositories.ClientRepository;
import com.example.otoportdeneme.services.ListingDetailService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/listings")
public class ListingDetailController {

    private final ListingDetailService listingDetailService;
    private final ClientRepository clientRepository;

    public ListingDetailController(ListingDetailService listingDetailService,
                                   ClientRepository clientRepository) {
        this.listingDetailService = listingDetailService;
        this.clientRepository = clientRepository;
    }

    @GetMapping("/{listingId}/detail")
    public ListingDetailDto getListingDetail(@PathVariable Long listingId) {

        Long clientId = null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getName() != null) {
            String email = auth.getName(); // uds.loadUserByUsername(email) -> username = email
            clientId = clientRepository.findIdByEmail(email).orElse(null);
        }

        return listingDetailService.getListingDetail(listingId, clientId);
    }
}
