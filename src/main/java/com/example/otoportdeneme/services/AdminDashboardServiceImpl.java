package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.AuditAction;
import com.example.otoportdeneme.dto_Objects.admin.*;
import com.example.otoportdeneme.models.AuditLog;
import com.example.otoportdeneme.models.Store;
import com.example.otoportdeneme.repositories.AuditLogRepository;
import com.example.otoportdeneme.repositories.StoreRepository;
import com.example.otoportdeneme.repositories.VisitLogRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final VisitLogRepository visitLogRepository;
    private final AuditLogRepository auditLogRepository;
    private final StoreRepository storeRepository;

    public AdminDashboardServiceImpl(
            VisitLogRepository visitLogRepository,
            AuditLogRepository auditLogRepository,
            StoreRepository storeRepository
    ) {
        this.visitLogRepository = visitLogRepository;
        this.auditLogRepository = auditLogRepository;
        this.storeRepository = storeRepository;
    }

    private ZoneId zone() {
        return ZoneId.of("Europe/Istanbul");
    }

    private Instant startOf(LocalDate date) {
        return date.atStartOfDay(zone()).toInstant();
    }

    private Instant endOf(LocalDate date) {
        return date.plusDays(1).atStartOfDay(zone()).toInstant();
    }

    @Override
    public DailyVisitStatsDto getDailyVisits(LocalDate date) {
        Instant start = startOf(date);
        Instant end = endOf(date);

        long total = visitLogRepository.countByCreatedAtBetween(start, end);

        Map<String, Long> byType = visitLogRepository.countByActorType(start, end).stream()
                .collect(Collectors.toMap(
                        r -> String.valueOf(r[0]),
                        r -> ((Number) r[1]).longValue()
                ));

        long guest = byType.getOrDefault("GUEST", 0L);
        long client = byType.getOrDefault("CLIENT", 0L);
        long store = byType.getOrDefault("STORE", 0L);

        return new DailyVisitStatsDto(total, guest, client, store);
    }

    @Override
    public HourlyTrafficDto getHourlyTraffic(LocalDate date) {
        Instant start = startOf(date);
        Instant end = endOf(date);

        Map<Integer, Long> map = new HashMap<>();
        for (Object[] r : visitLogRepository.countHourly(start, end)) {
            int h = ((Number) r[0]).intValue();
            long c = ((Number) r[1]).longValue();
            map.put(h, c);
        }

        List<HourlyTrafficDto.HourCount> out = new ArrayList<>();
        for (int h = 0; h < 24; h++) {
            out.add(new HourlyTrafficDto.HourCount(h, map.getOrDefault(h, 0L)));
        }

        return new HourlyTrafficDto(out);
    }

    @Override
    public List<StoreListingActivityDto> getStoreListingActivity(LocalDate date) {
        Instant start = startOf(date);
        Instant end = endOf(date);

        Map<Long, Long> creates = toCountMap(auditLogRepository.countStoreListingActions(AuditAction.CREATE, start, end));
        Map<Long, Long> deletes = toCountMap(auditLogRepository.countStoreListingActions(AuditAction.DELETE, start, end));
        Map<Long, Long> updates = toCountMap(auditLogRepository.countStoreListingActions(AuditAction.UPDATE, start, end));

        List<Store> stores = storeRepository.findAll();

        return stores.stream()
                .map(s -> new StoreListingActivityDto(
                        s.getId(),
                        s.getStoreName(),
                        creates.getOrDefault(s.getId(), 0L),
                        deletes.getOrDefault(s.getId(), 0L),
                        updates.getOrDefault(s.getId(), 0L)
                ))
                .sorted(Comparator.comparingLong((StoreListingActivityDto x) -> (x.getCreates() + x.getDeletes() + x.getUpdates())).reversed())
                .toList();
    }

    @Override
    public List<AuditRowDto> getRecentAudit(LocalDate date, int limit) {
        if (limit <= 0) limit = 50;
        if (limit > 200) limit = 200;

        Instant start = startOf(date);
        Instant end = endOf(date);

        List<AuditLog> logs = auditLogRepository
                .findByCreatedAtBetweenOrderByCreatedAtDesc(start, end, PageRequest.of(0, limit));


        return logs.stream()
                .map(a -> new AuditRowDto(
                        a.getCreatedAt(),
                        a.getActorType() == null ? null : a.getActorType().name(),
                        a.getActorId(),
                        a.getAction() == null ? null : a.getAction().name(),
                        a.getEntityType(),
                        a.getEntityId(),
                        a.getDetails()
                ))
                .toList();
    }

    private Map<Long, Long> toCountMap(List<Object[]> rows) {
        Map<Long, Long> m = new HashMap<>();
        for (Object[] r : rows) {
            Long id = ((Number) r[0]).longValue();
            Long c = ((Number) r[1]).longValue();
            m.put(id, c);
        }
        return m;
    }
}
