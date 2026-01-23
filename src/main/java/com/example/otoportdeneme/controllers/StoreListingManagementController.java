package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Objects.StoreListingEditDto;
import com.example.otoportdeneme.dto_Requests.StoreCarUpdateRequest;
import com.example.otoportdeneme.repositories.UserAccountRepository;
import com.example.otoportdeneme.services.StoreListingService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.example.otoportdeneme.Enums.ActorType;
import com.example.otoportdeneme.Enums.AuditAction;
import com.example.otoportdeneme.services.AuditService;

import java.util.Map;

@RestController
@RequestMapping("/api/store/listings")
public class StoreListingManagementController {

    private final StoreListingService storeListingService;
    private final UserAccountRepository userAccountRepository;
    private final AuditService auditService;

    public StoreListingManagementController(StoreListingService storeListingService,
                                            UserAccountRepository userAccountRepository,
                                            AuditService auditService) {
        this.storeListingService = storeListingService;
        this.userAccountRepository = userAccountRepository;
        this.auditService = auditService;
    }

    // ✅ edit form data
    @GetMapping("/{listingId}")
    public StoreListingEditDto getForEdit(Authentication auth, @PathVariable Long listingId) {
        Long storeId = resolveUserId(auth);
        return storeListingService.getMyListingForEdit(storeId, listingId);
    }

    // ✅ update
    @PutMapping("/{listingId}")
    public StoreListingEditDto update(Authentication auth,
                                      @PathVariable Long listingId,
                                      @Valid @RequestBody StoreCarUpdateRequest req,
                                      HttpServletRequest httpReq) {

        System.out.println(">>> UPDATE REQ expertReport is null? " + (req.getExpertReport() == null));
        if (req.getExpertReport() != null) {
            System.out.println(">>> expertReport.items size = " +
                    (req.getExpertReport().getItems() == null ? "null" : req.getExpertReport().getItems().size()));
            if (req.getExpertReport().getItems() != null && !req.getExpertReport().getItems().isEmpty()) {
                System.out.println(">>> first item part=" + req.getExpertReport().getItems().get(0).getPart()
                        + " status=" + req.getExpertReport().getItems().get(0).getStatus());
            }
        }

        Long storeId = resolveUserId(auth);
        StoreListingEditDto out = storeListingService.updateMyListing(storeId, listingId, req);

        auditService.log(
                ActorType.STORE, storeId,
                AuditAction.UPDATE,
                "LISTING", listingId,
                "{\"note\":\"store updated listing\"}",
                resolveClientIp(httpReq),
                httpReq.getHeader("User-Agent")
        );

        return out;
    }

    // ✅ delete
    @DeleteMapping("/{listingId}")
    public Map<String, Object> delete(Authentication auth,
                                      @PathVariable Long listingId,
                                      HttpServletRequest httpReq) {

        Long storeId = resolveUserId(auth);
        storeListingService.deleteMyListing(storeId, listingId);

        auditService.log(
                ActorType.STORE, storeId,
                AuditAction.DELETE,
                "LISTING", listingId,
                "{\"note\":\"store deleted listing\"}",
                resolveClientIp(httpReq),
                httpReq.getHeader("User-Agent")
        );

        return Map.of("ok", true);
    }

    private Long resolveUserId(Authentication auth) {
        if (auth == null) throw new IllegalArgumentException("Unauthorized");
        String email = auth.getName();

        return userAccountRepository.findIdByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    }

    // ✅ EKLE: ip resolver (hata buradan geliyordu)
    private String resolveClientIp(HttpServletRequest req) {
        String xff = req.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        String xri = req.getHeader("X-Real-IP");
        if (xri != null && !xri.isBlank()) {
            return xri.trim();
        }
        return req.getRemoteAddr();
    }
}
