package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Objects.admin.WalRowDto;
import com.example.otoportdeneme.dto_Requests.WalSearchRequest;
import com.example.otoportdeneme.services.WalService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/wal")
public class AdminWalController {

    private final WalService walService;

    public AdminWalController(WalService walService) {
        this.walService = walService;
    }

    @GetMapping("/recent")
    public List<WalRowDto> recent(
            @RequestParam(defaultValue = "100") int limit,
            @RequestParam(defaultValue = "desc") String sort
    ) {
        return walService.recent(limit, sort);
    }

    @PostMapping(value = "/search", consumes = MediaType.APPLICATION_JSON_VALUE)
    public List<WalRowDto> search(@RequestBody WalSearchRequest req) {
        return walService.search(req);
    }
}
