package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Requests.InquiryUpsertRequest;
import com.example.otoportdeneme.dto_Response.InquiryUpsertResponse;
import com.example.otoportdeneme.repositories.ClientRepository;
import com.example.otoportdeneme.services.InquiryClientService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/client/inquiries")
public class ClientInquiryController {

    private final InquiryClientService inquiryClientService;
    private final ClientRepository clientRepository;

    public ClientInquiryController(InquiryClientService inquiryClientService,
                                   ClientRepository clientRepository) {
        this.inquiryClientService = inquiryClientService;
        this.clientRepository = clientRepository;
    }

    @PostMapping("/upsert")
    public InquiryUpsertResponse upsert(Authentication auth,
                                        @RequestBody InquiryUpsertRequest req,
                                        @RequestHeader(value = "X-Forwarded-For", required = false) String ip,
                                        @RequestHeader(value = "User-Agent", required = false) String ua) {

        Long clientId = resolveClientId(auth);

        return inquiryClientService.upsert(
                clientId,
                req.getListingId(),
                req.getMessage(),
                ip,
                ua
        );
    }

    @GetMapping("/thread")
    public InquiryUpsertResponse thread(Authentication auth,
                                        @RequestParam Long listingId) {

        Long clientId = resolveClientId(auth);
        return inquiryClientService.getThread(clientId, listingId);
    }

    private Long resolveClientId(Authentication auth) {
        if (auth == null) throw new IllegalArgumentException("Unauthorized");
        return clientRepository.findIdByEmail(auth.getName())
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));
    }
}
