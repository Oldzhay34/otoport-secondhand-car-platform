package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.Enums.ActorType;
import com.example.otoportdeneme.Enums.AuditAction;
import com.example.otoportdeneme.dto_Objects.StoreListingCreatedDto;
import com.example.otoportdeneme.dto_Objects.StoreListingCreatedMapper;
import com.example.otoportdeneme.dto_Requests.StoreListingCreateRequest;
import com.example.otoportdeneme.dto_Response.StoreListingCreateResponse;
import com.example.otoportdeneme.models.Listing;
import com.example.otoportdeneme.repositories.StoreRepository;
import com.example.otoportdeneme.services.AuditService;
import com.example.otoportdeneme.services.StoreCreateListingService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/api/store/listings")
public class StoreCreateListingController {

    private final StoreCreateListingService service;
    private final StoreRepository storeRepository;
    private final AuditService auditService;

    public StoreCreateListingController(StoreCreateListingService service,
                                        StoreRepository storeRepository,
                                        AuditService auditService) {
        this.service = service;
        this.storeRepository = storeRepository;
        this.auditService = auditService;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public StoreListingCreateResponse create(
            Authentication auth,
            @RequestPart("data") StoreListingCreateRequest req,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            HttpServletRequest httpReq
    ) {
        Long storeId = resolveStoreId(auth);

        Listing created = service.createMyListing(storeId, req, images);

        // ✅ AUDIT
        auditService.log(
                ActorType.STORE, storeId,
                AuditAction.CREATE,
                "LISTING", created.getId(),
                toJsonCreateDetails(req, created.getId()),
                resolveClientIp(httpReq),
                httpReq.getHeader("User-Agent")
        );

        StoreListingCreatedDto dto = StoreListingCreatedMapper.toDto(created);
        return new StoreListingCreateResponse(dto);
    }

    private Long resolveStoreId(Authentication auth) {
        if (auth == null) throw new IllegalArgumentException("Unauthorized");
        String email = auth.getName();
        return storeRepository.findIdByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));
    }

    private String resolveClientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) return xff.split(",")[0].trim();
        String xri = req.getHeader("X-Real-IP");
        if (xri != null && !xri.isBlank()) return xri.trim();
        return req.getRemoteAddr();
    }

    private String esc(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").trim();
    }

    private String toJsonCreateDetails(StoreListingCreateRequest req, Long listingId) {
        String price = req.getPrice() == null ? "" : req.getPrice().toPlainString();
        String year = req.getYear() == null ? "null" : req.getYear().toString();

        return ("{" +
                "\"listingId\":" + listingId + "," +
                "\"title\":\"" + esc(req.getTitle()) + "\"," +
                "\"price\":\"" + esc(price) + "\"," +
                "\"currency\":\"" + esc(req.getCurrency() == null ? "" : req.getCurrency().toUpperCase(Locale.ROOT)) + "\"," +
                "\"city\":\"" + esc(req.getCity()) + "\"," +
                "\"district\":\"" + esc(req.getDistrict()) + "\"," +
                "\"brand\":\"" + esc(req.getBrand()) + "\"," +
                "\"model\":\"" + esc(req.getModel()) + "\"," +
                "\"year\":" + year +
                "}");
    }
}
