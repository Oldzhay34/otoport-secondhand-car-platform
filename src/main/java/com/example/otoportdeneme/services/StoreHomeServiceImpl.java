package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Objects.StoreHomeDto;
import com.example.otoportdeneme.dto_Objects.StoreListingRowDto;
import com.example.otoportdeneme.models.Listing;
import com.example.otoportdeneme.models.Store;
import com.example.otoportdeneme.repositories.ListingRepository;
import com.example.otoportdeneme.repositories.StoreRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class StoreHomeServiceImpl implements StoreHomeService {

    private final StoreRepository storeRepository;
    private final ListingRepository listingRepository;
    private final JwtService jwtService; // ✅ eklendi

    public StoreHomeServiceImpl(StoreRepository storeRepository,
                                ListingRepository listingRepository,
                                JwtService jwtService) { // ✅ eklendi
        this.storeRepository = storeRepository;
        this.listingRepository = listingRepository;
        this.jwtService = jwtService;
    }

    @Override
    public StoreHomeDto getMyHome(String q) {
        Long storeId = requireStoreIdFromToken();

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Store not found"));

        List<Listing> listings = (q == null || q.isBlank())
                ? listingRepository.findByStoreIdWithImagesOrderByCreatedAtDesc(storeId)
                : listingRepository.findByStoreIdAndTitleContainingIgnoreCaseWithImagesOrderByCreatedAtDesc(storeId, q);


        StoreHomeDto dto = new StoreHomeDto();
        dto.setStoreId(store.getId());
        dto.setStoreName(store.getStoreName());
        dto.setCity(store.getCity());
        dto.setDistrict(store.getDistrict());
        dto.setVerified(store.getVerified());

        dto.setListings(listings.stream().map(this::toRow).collect(Collectors.toList()));
        return dto;
    }

    private Long requireStoreIdFromToken() {

        ServletRequestAttributes attrs =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (attrs == null || attrs.getRequest() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        String auth = attrs.getRequest().getHeader(HttpHeaders.AUTHORIZATION);

        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing token");
        }

        String token = auth.substring(7);

        try {
            var claims = jwtService.parse(token).getBody();

            // ✅ role kontrolü (opsiyonel ama tavsiye)
            Object roleObj = claims.get("role");
            String role = roleObj != null ? roleObj.toString() : null;
            if (role == null || !role.equalsIgnoreCase("STORE")) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a STORE token");
            }

            Object idObj = claims.get("id");
            if (idObj == null) {
                throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token missing id claim");
            }

            if (idObj instanceof Integer) return ((Integer) idObj).longValue();
            if (idObj instanceof Long) return (Long) idObj;
            if (idObj instanceof String) return Long.parseLong((String) idObj);

            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unsupported id claim type");

        } catch (ResponseStatusException e) {
            throw e;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid token");
        }
    }


    private StoreListingRowDto toRow(Listing l) {
        StoreListingRowDto r = new StoreListingRowDto();
        r.setId(l.getId());
        r.setTitle(l.getTitle());
        r.setStatus(l.getStatus() != null ? l.getStatus().name() : null);
        r.setPrice(l.getPrice()); // BigDecimal olmalı
        r.setCurrency(l.getCurrency());
        r.setCreatedAt(l.getCreatedAt());

        String cover = l.getCoverImageUrl(); // ✅ Listing'te zaten var
        r.setCoverImageUrl(cover);

        return r;
    }

    /**
     * ✅ Token içindeki "id" claim'inden storeId çözer.
     * AuthController token üretirken claims.put("id", ua.getId()) koymuş olmalı.
     */
    private Long requireStoreIdFromAuth() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        Object principal = auth.getPrincipal();

        // ✅ email çöz
        String email = null;

        if (principal instanceof UserDetails) {
            email = ((UserDetails) principal).getUsername(); // genelde email
        } else if (principal instanceof String) {
            // bazen "anonymousUser" gelebilir
            String s = (String) principal;
            if (!"anonymousUser".equalsIgnoreCase(s)) email = s;
        } else {
            // custom principal olabilir
            try {
                var m = principal.getClass().getMethod("getUsername");
                Object u = m.invoke(principal);
                if (u != null) email = u.toString();
            } catch (Exception ignored) {}
        }

        if (email == null || email.isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Cannot resolve email from auth");
        }

        // ✅ email -> store id
        return storeRepository.findIdByEmail(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not a STORE account"));
    }
}
