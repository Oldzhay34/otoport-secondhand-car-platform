package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Objects.StoreHomeDto;
import com.example.otoportdeneme.services.StoreHomeService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/store/home")
public class StoreHomeController {

    private final StoreHomeService storeHomeService;

    public StoreHomeController(StoreHomeService storeHomeService) {
        this.storeHomeService = storeHomeService;
    }

    // ✅ token’dan store bulunur, home datası döner
    @GetMapping
    public StoreHomeDto getMyHome(@RequestParam(required = false) String q) {
        return storeHomeService.getMyHome(q);
    }
}
