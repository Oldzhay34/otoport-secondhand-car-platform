package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Objects.admin.AuditRowDto;
import com.example.otoportdeneme.dto_Requests.AuditSearchRequest;
import com.example.otoportdeneme.models.AuditLog;
import com.example.otoportdeneme.repositories.AuditLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminAuditLogServiceImpl implements AdminAuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AdminAuditLogServiceImpl(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Override
    public List<AuditRowDto> search(AuditSearchRequest req) {
        int limit = (req.getLimit() == null || req.getLimit() <= 0) ? 100 : Math.min(req.getLimit(), 1000);

        Specification<AuditLog> spec = Specification
                .where(AuditLogSpecifications.actorTypeIs(req.getActorType()))
                .and(AuditLogSpecifications.actorIdIs(req.getActorId()))
                .and(AuditLogSpecifications.actionIs(req.getAction()))
                .and(AuditLogSpecifications.entityTypeIs(req.getEntityType()))
                .and(AuditLogSpecifications.entityIdIs(req.getEntityId()))
                .and(AuditLogSpecifications.detailsLike(req.getQ()));

        var pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        return auditLogRepository.findAll(spec, pageable).getContent()
                .stream()
                .map(this::toDto)
                .toList();

    }
    @Override
    public List<AuditRowDto> getRecent(int limit, String sort) {
        int safeLimit = Math.max(1, Math.min(limit <= 0 ? 100 : limit, 500));
        Pageable pageable = PageRequest.of(0, safeLimit);

        List<AuditLog> logs = "asc".equalsIgnoreCase(sort)
                ? auditLogRepository.findAllByOrderByCreatedAtAsc(pageable)
                : auditLogRepository.findAllByOrderByCreatedAtDesc(pageable);

        return logs.stream().map(this::toDto).collect(Collectors.toList());
    }

    private AuditRowDto toDto(AuditLog a) {
        return new AuditRowDto(
                a.getCreatedAt(),
                a.getActorType() == null ? null : a.getActorType().name(),
                a.getActorId(),
                a.getAction() == null ? null : a.getAction().name(),
                a.getEntityType(),
                a.getEntityId(),
                a.getDetails(),
                a.getIpAddress(),
                a.getUserAgent()
        );
    }
}
