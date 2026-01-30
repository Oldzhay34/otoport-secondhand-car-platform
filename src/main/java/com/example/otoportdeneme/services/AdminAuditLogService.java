package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Objects.admin.AuditRowDto;
import com.example.otoportdeneme.dto_Requests.AuditSearchRequest;

import java.util.List;

public interface AdminAuditLogService {
    List<AuditRowDto> search(AuditSearchRequest req);
    List<AuditRowDto> getRecent(int limit, String sort);

    void write(String action, String details);

    // ✅ önerilen: entity bilgili
    void write(String action, String entityType, Long entityId, String details);
}
