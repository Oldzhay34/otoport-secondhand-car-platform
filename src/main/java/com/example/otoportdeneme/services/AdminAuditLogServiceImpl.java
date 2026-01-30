package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.ActorType;
import com.example.otoportdeneme.Enums.AuditAction;
import com.example.otoportdeneme.dto_Objects.admin.AuditRowDto;
import com.example.otoportdeneme.dto_Requests.AuditSearchRequest;
import com.example.otoportdeneme.models.AuditLog;
import com.example.otoportdeneme.models.UserAccount;
import com.example.otoportdeneme.repositories.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AdminAuditLogServiceImpl implements AdminAuditLogService {

    private final AuditLogRepository auditLogRepository;
    private final CustomUserDetailsService uds; // ✅ EKLENDİ

    public AdminAuditLogServiceImpl(AuditLogRepository auditLogRepository,
                                    CustomUserDetailsService uds) { // ✅ EKLENDİ
        this.auditLogRepository = auditLogRepository;
        this.uds = uds;
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

    @Override
    public void write(String action, String details) {
        write(action, null, null, details);
    }

    @Override
    public void write(String action, String entityType, Long entityId, String details) {
        try {
            // 1) Action parse
            AuditAction auditAction;
            try {
                auditAction = AuditAction.valueOf(action);
            } catch (Exception e) {
                auditAction = AuditAction.UPDATE;
                details = "[UNKNOWN_ACTION=" + action + "] " + (details == null ? "" : details);
            }

            // 2) Request bilgileri (ip + ua)
            HttpServletRequest req = null;
            try {
                ServletRequestAttributes attrs =
                        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attrs != null) req = attrs.getRequest();
            } catch (Exception ignored) {}

            String ip = null;
            String ua = null;
            if (req != null) {
                ua = req.getHeader("User-Agent");
                ip = req.getHeader("X-Forwarded-For");
                if (ip == null || ip.isBlank()) ip = req.getRemoteAddr();
                if (ip != null && ip.contains(",")) ip = ip.split(",")[0].trim();
            }

            // 3) Actor resolve (SecurityContext + domain user)
            ActorType actorType = ActorType.GUEST; // default
            Long actorId = null;

            Authentication auth = SecurityContextHolder.getContext().getAuthentication();

            if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
                // auth.getName() = email
                String email = auth.getName();

                try {
                    UserAccount uaDomain = uds.loadDomainUserByEmail(email);
                    actorType = (uaDomain.getActorType() == null) ? ActorType.SYSTEM : uaDomain.getActorType();
                    actorId = uaDomain.getId();
                } catch (Exception e) {
                    // Domain user bulunamazsa role’dan tahmin
                    boolean isAdmin = auth.getAuthorities().stream()
                            .anyMatch(g -> g.getAuthority().equals("ROLE_ADMIN") || g.getAuthority().equals("ADMIN"));
                    boolean isStore = auth.getAuthorities().stream()
                            .anyMatch(g -> g.getAuthority().equals("ROLE_STORE") || g.getAuthority().equals("STORE"));

                    if (isAdmin) actorType = ActorType.ADMIN;
                    else if (isStore) actorType = ActorType.STORE;
                    else actorType = ActorType.CLIENT;
                }
            } else {
                actorType = ActorType.GUEST;
            }

            // ✅ actorType NOT NULL
            if (actorType == null) actorType = ActorType.SYSTEM;

            // 4) Save
            AuditLog log = new AuditLog();
            log.setAction(auditAction);
            log.setActorType(actorType);
            log.setActorId(actorId);

            log.setEntityType(entityType);
            log.setEntityId(entityId);

            log.setDetails(details);
            log.setIpAddress(ip);
            log.setUserAgent(ua);

            auditLogRepository.save(log);

        } catch (Exception e) {
            // audit patlarsa akışı bozmasın
            System.out.println("[AUDIT_WRITE_FAILED] " + e.getMessage());
        }
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
