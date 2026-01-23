package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.Enums.ActorType;
import com.example.otoportdeneme.dto_Objects.admin.AuditRowDto;
import com.example.otoportdeneme.dto_Requests.AuditSearchRequest;
import com.example.otoportdeneme.services.AdminAuditLogService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/audit-logs")
public class AdminAuditLogController {

    private final AdminAuditLogService adminAuditLogService;

    public AdminAuditLogController(AdminAuditLogService adminAuditLogService) {
        this.adminAuditLogService = adminAuditLogService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADMIN')")
    public List<AuditRowDto> list(
            @RequestParam(required = false) ActorType actorType,
            @RequestParam(required = false) Long actorId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String entityType,
            @RequestParam(required = false) Long entityId,
            @RequestParam(required = false, name = "q") String q,
            @RequestParam(required = false, defaultValue = "100") Integer limit,
            @RequestParam(required = false, defaultValue = "desc") String sort // ✅ sort burada dursun
    ) {
        AuditSearchRequest req = new AuditSearchRequest();
        req.setActorType(actorType);
        req.setActorId(actorId);
        req.setAction(action);
        req.setEntityType(entityType);
        req.setEntityId(entityId);
        req.setQ(q);
        req.setLimit(limit);
        req.setSort(sort);

        return adminAuditLogService.search(req);
    }
    @GetMapping("/recent")
    @PreAuthorize("hasRole('ADMIN') or hasAuthority('ADMIN')")
    public List<AuditRowDto> getRecent(
            @RequestParam(defaultValue = "200") int limit,
            @RequestParam(defaultValue = "desc") String sort
    ) {
        return adminAuditLogService.getRecent(limit, sort);
    }
}
