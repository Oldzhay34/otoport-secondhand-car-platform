package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Objects.FavoriteCardDto;
import com.example.otoportdeneme.repositories.ClientRepository;
import com.example.otoportdeneme.repositories.FavoriteRepository;
import com.example.otoportdeneme.services.FavoriteService;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/client/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;
    private final FavoriteRepository favoriteRepository;
    private final ClientRepository clientRepository;

    public FavoriteController(FavoriteService favoriteService,
                              FavoriteRepository favoriteRepository,
                              ClientRepository clientRepository) {
        this.favoriteService = favoriteService;
        this.favoriteRepository = favoriteRepository;
        this.clientRepository = clientRepository;
    }

    private Long resolveClientId(Authentication auth) {
        if (auth == null || auth.getName() == null || auth.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized");
        }

        return clientRepository.findIdByEmail(auth.getName().trim())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized"));
    }

    @GetMapping
    public List<FavoriteCardDto> myFavorites(Authentication auth) {
        Long clientId = resolveClientId(auth);
        return favoriteRepository.findCardsByClientId(clientId);
    }

    @PostMapping("/{listingId}")
    public void add(Authentication auth, @PathVariable Long listingId) {
        Long clientId = resolveClientId(auth);
        favoriteService.addFavorite(clientId, listingId);
    }

    @DeleteMapping("/{listingId}")
    public void remove(Authentication auth, @PathVariable Long listingId) {
        Long clientId = resolveClientId(auth);
        favoriteService.removeFavorite(clientId, listingId);
    }
}
