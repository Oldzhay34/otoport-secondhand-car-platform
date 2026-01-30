package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Requests.InquiryReplyRequest;
import com.example.otoportdeneme.dto_Requests.InquiryUpsertRequest;
import com.example.otoportdeneme.dto_Response.InquiryUpsertResponse;
import com.example.otoportdeneme.services.InquiryPublicService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inquiries")
public class InquiryPublicController {

    private final InquiryPublicService inquiryPublicService;

    public InquiryPublicController(InquiryPublicService inquiryPublicService) {
        this.inquiryPublicService = inquiryPublicService;
    }

    // ✅ vehicleinfo: inquiry yoksa açar + ilk mesajı yazar, varsa mevcut inquiry'ye mesaj ekler (upsert)
    @PostMapping
    public InquiryUpsertResponse upsert(
            Authentication auth,
            @RequestBody InquiryUpsertRequest req,
            @RequestHeader(value = "X-Forwarded-For", required = false) String ip,
            @RequestHeader(value = "User-Agent", required = false) String ua
    ) {
        String email = (auth != null ? auth.getName() : null);
        return inquiryPublicService.upsert(req, email, ip, ua);
    }

    // ✅ login client için: bu listing'de inquiry var mı?
    @GetMapping("/by-listing/{listingId}")
    public InquiryUpsertResponse getByListing(Authentication auth, @PathVariable Long listingId) {
        String email = (auth != null ? auth.getName() : null);
        return inquiryPublicService.getThreadByListing(listingId, email);
    }

    // ✅ thread getir
    @GetMapping("/{inquiryId}")
    public InquiryUpsertResponse getThread(Authentication auth, @PathVariable Long inquiryId) {
        String email = (auth != null ? auth.getName() : null);
        return inquiryPublicService.getThread(inquiryId, email);
    }

    // ✅ mevcut inquiry'ye cevap (client veya guest)
    // guest için header ile guestEmail gönderiyoruz (UI tarafında localStorage ile de yollayabilirsin)
    @PostMapping("/{inquiryId}/reply")
    public InquiryUpsertResponse reply(
            Authentication auth,
            @PathVariable Long inquiryId,
            @RequestBody InquiryReplyRequest req,
            @RequestHeader(value = "X-Guest-Email", required = false) String guestEmail,
            @RequestHeader(value = "X-Forwarded-For", required = false) String ip,
            @RequestHeader(value = "User-Agent", required = false) String ua
    ) {
        String email = (auth != null ? auth.getName() : null);
        String msg = req != null ? req.getMessage() : null;
        return inquiryPublicService.reply(inquiryId, msg, email, guestEmail, ip, ua);
    }
}
