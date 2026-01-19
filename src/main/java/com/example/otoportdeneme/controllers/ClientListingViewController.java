package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.services.JwtService;
import com.example.otoportdeneme.services.ListingViewService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@RestController
@RequestMapping("/api/client/listings")
public class ClientListingViewController {

    private final ListingViewService listingViewService;
    private final JwtService jwtService;

    public ClientListingViewController(ListingViewService listingViewService,
                                       JwtService jwtService) {
        this.listingViewService = listingViewService;
        this.jwtService = jwtService;
    }

    @PostMapping("/{listingId}/view")
    public void registerView(@PathVariable Long listingId) {

        String token = getBearerToken();
        var claims = jwtService.parse(token).getBody();

        Object roleObj = claims.get("role");
        String role = roleObj != null ? roleObj.toString() : null;
        if (role == null || !role.equalsIgnoreCase("CLIENT")) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not a CLIENT token");
        }

        Object idObj = claims.get("id");
        if (idObj == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token missing id");

        Long userId;
        if (idObj instanceof Integer) userId = ((Integer) idObj).longValue();
        else if (idObj instanceof Long) userId = (Long) idObj;
        else userId = Long.parseLong(idObj.toString());

        listingViewService.registerViewOnce(userId, listingId);
    }

    private String getBearerToken() {
        ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attrs == null || attrs.getRequest() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }
        String auth = attrs.getRequest().getHeader(HttpHeaders.AUTHORIZATION);
        if (auth == null || !auth.startsWith("Bearer ")) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing token");
        }
        return auth.substring(7);
    }
}
