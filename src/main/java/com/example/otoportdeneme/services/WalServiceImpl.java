package com.example.otoportdeneme.services;

import com.example.otoportdeneme.dto_Objects.admin.WalRowDto;
import com.example.otoportdeneme.dto_Requests.WalSearchRequest;
import com.example.otoportdeneme.models.WalEntry;
import com.example.otoportdeneme.repositories.WalEntryRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Instant;
import java.util.Base64;
import java.util.List;

@Service
public class WalServiceImpl implements WalService {

    private final WalEntryRepository repo;

    public WalServiceImpl(WalEntryRepository repo) {
        this.repo = repo;
    }

    @Override
    public void appendAdminHttp(String actorType, Long actorId,
                                String method, String path, String queryString,
                                Integer status, String ip, String userAgent,
                                String requestBody, String responseBody) {

        WalEntry last = repo.findTopByOrderByIdDesc();
        String prevHash = last == null ? null : last.getHash();

        WalEntry e = new WalEntry();
        e.setActorType(actorType);
        e.setActorId(actorId);

        e.setMethod(safe(method, 16));
        e.setPath(safe(path, 512));
        e.setQueryString(safe(queryString, 2048));

        e.setStatus(status);
        e.setIpAddress(safe(ip, 64));
        e.setUserAgent(safe(userAgent, 512));

        e.setRequestBody(requestBody);
        e.setResponseBody(responseBody);

        e.setPrevHash(prevHash);
        e.setHash(calcHash(e));

        repo.save(e);
    }

    @Override
    public List<WalRowDto> recent(int limit, String sort) {
        int safeLimit = Math.max(1, Math.min(limit <= 0 ? 100 : limit, 500));
        Pageable pageable = PageRequest.of(0, safeLimit);

        List<WalEntry> rows = "asc".equalsIgnoreCase(sort)
                ? repo.findAllByOrderByCreatedAtAsc(pageable)
                : repo.findAllByOrderByCreatedAtDesc(pageable);

        return rows.stream().map(this::toDto).toList();
    }

    @Override
    public List<WalRowDto> search(WalSearchRequest req) {
        int limit = (req.getLimit() == null || req.getLimit() <= 0) ? 100 : Math.min(req.getLimit(), 500);

        Instant from = parseInstant(req.getFrom());
        Instant to = parseInstant(req.getTo());

        Specification<WalEntry> spec = Specification
                .where(WalSpecifications.actorTypeIs(req.getActorType()))
                .and(WalSpecifications.actorIdIs(req.getActorId()))
                .and(WalSpecifications.methodIs(req.getMethod()))
                .and(WalSpecifications.statusIs(req.getStatus()))
                .and(WalSpecifications.pathContains(req.getPathContains()))
                .and(WalSpecifications.createdAtBetween(from, to))
                .and(WalSpecifications.bodyLike(req.getQ()));

        Sort s = "asc".equalsIgnoreCase(req.getSort())
                ? Sort.by(Sort.Direction.ASC, "createdAt")
                : Sort.by(Sort.Direction.DESC, "createdAt");

        var pageable = PageRequest.of(0, limit, s);

        return repo.findAll(spec, pageable).getContent().stream().map(this::toDto).toList();
    }

    // ---- helpers ----

    private WalRowDto toDto(WalEntry a) {
        return new WalRowDto(
                a.getId(),
                a.getCreatedAt(),
                a.getActorType(),
                a.getActorId(),
                a.getMethod(),
                a.getPath(),
                a.getQueryString(),
                a.getStatus(),
                a.getIpAddress(),
                a.getUserAgent(),
                a.getRequestBody(),
                a.getResponseBody(),
                a.getPrevHash(),
                a.getHash()
        );
    }

    private static String safe(String s, int max) {
        if (s == null) return null;
        s = s.trim();
        return s.length() <= max ? s : s.substring(0, max);
    }

    private static Instant parseInstant(String s) {
        if (s == null || s.isBlank()) return null;
        try { return Instant.parse(s.trim()); } catch (Exception e) { return null; }
    }

    private static String calcHash(WalEntry e) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            String payload =
                    String.valueOf(e.getCreatedAt()) + "|" +
                            String.valueOf(e.getActorType()) + "|" +
                            String.valueOf(e.getActorId()) + "|" +
                            String.valueOf(e.getMethod()) + "|" +
                            String.valueOf(e.getPath()) + "|" +
                            String.valueOf(e.getQueryString()) + "|" +
                            String.valueOf(e.getStatus()) + "|" +
                            String.valueOf(e.getIpAddress()) + "|" +
                            String.valueOf(e.getUserAgent()) + "|" +
                            String.valueOf(e.getRequestBody()) + "|" +
                            String.valueOf(e.getResponseBody()) + "|" +
                            String.valueOf(e.getPrevHash());
            byte[] digest = md.digest(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(digest);
        } catch (Exception ex) {
            return null; // hash şart değil; DB insert'i bozmasın
        }
    }
}
