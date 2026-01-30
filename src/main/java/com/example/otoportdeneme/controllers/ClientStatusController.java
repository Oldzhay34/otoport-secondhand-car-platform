package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Objects.admin.ClientStatusDto;
import com.example.otoportdeneme.dto_Requests.ClientStatusUpdateRequest;
import com.example.otoportdeneme.services.ClientStatusService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/clients")
public class ClientStatusController {

    private final ClientStatusService service;

    public ClientStatusController(ClientStatusService service) {
        this.service = service;
    }

    @GetMapping("/status")
    public ResponseEntity<List<ClientStatusDto>> listAll() {
        return ResponseEntity.ok(service.listAllClients());
    }

    // Tekli toggle / set
    @PatchMapping("/{id}/status")
    public ResponseEntity<ClientStatusDto> updateStatus(
            @PathVariable Long id,
            @RequestBody(required = false) ClientStatusUpdateRequest req
    ) {
        return ResponseEntity.ok(service.setClientStatus(id, req == null ? null : req.getStatus()));
    }

    // Toplu
    @PostMapping("/status/bulk")
    public ResponseEntity<List<ClientStatusDto>> bulk(@RequestBody ClientStatusUpdateRequest req) {
        return ResponseEntity.ok(service.bulkSetStatus(req.getClientIds(), req.getStatus()));
    }
}
