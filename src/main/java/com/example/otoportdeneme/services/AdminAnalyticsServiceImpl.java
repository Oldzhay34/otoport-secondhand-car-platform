package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.AuditAction;
import com.example.otoportdeneme.dto_Response.AdminDailyStatsDto;
import com.example.otoportdeneme.dto_Response.HourlyPointDto;
import com.example.otoportdeneme.dto_Response.StoreActivityDto;
import com.example.otoportdeneme.dto_Objects.admin.SpamAttemptActorDto;
import com.example.otoportdeneme.repositories.*;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.*;

@Service
public class AdminAnalyticsServiceImpl implements AdminAnalyticsService {

    private final GuestAccessLogRepository guestRepo;
    private final AuditLogRepository auditRepo;
    private final InquiryRepository inquiryRepo;
    private final InquiryMessageRepository msgRepo;
    private final MessageModerationAttemptRepository attemptRepo; // ✅

    public AdminAnalyticsServiceImpl(GuestAccessLogRepository guestRepo,
                                     AuditLogRepository auditRepo,
                                     InquiryRepository inquiryRepo,
                                     InquiryMessageRepository msgRepo,
                                     MessageModerationAttemptRepository attemptRepo // ✅
    ) {
        this.guestRepo = guestRepo;
        this.auditRepo = auditRepo;
        this.inquiryRepo = inquiryRepo;
        this.msgRepo = msgRepo;
        this.attemptRepo = attemptRepo;
    }

    @Override
    public AdminDailyStatsDto daily(LocalDate date) {
        Range r = dayRange(date);

        long guestCount = guestRepo.countByCreatedAtBetween(r.start, r.end);

        Map<String, Long> byRole = new LinkedHashMap<>();
        long totalLogin = 0;

        List<Object[]> rows = auditRepo.countByActorTypeForAction(AuditAction.LOGIN, r.start, r.end);
        for (Object[] row : rows) {
            String actorType = String.valueOf(row[0]);
            long cnt = ((Number) row[1]).longValue();
            byRole.put(actorType, cnt);
            totalLogin += cnt;
        }

        return new AdminDailyStatsDto(date.toString(), guestCount, byRole, totalLogin);
    }

    @Override
    public List<HourlyPointDto> hourly(LocalDate date) {
        Range r = dayRange(date);
        long[] buckets = new long[24];

        for (Object[] row : auditRepo.countHourlyAll(r.start, r.end)) {
            int h = ((Number) row[0]).intValue();
            long c = ((Number) row[1]).longValue();
            if (h >= 0 && h < 24) buckets[h] += c;
        }

        for (Object[] row : guestRepo.countHourly(r.start, r.end)) {
            int h = ((Number) row[0]).intValue();
            long c = ((Number) row[1]).longValue();
            if (h >= 0 && h < 24) buckets[h] += c;
        }

        for (Object[] row : msgRepo.countHourly(r.start, r.end)) {
            int h = ((Number) row[0]).intValue();
            long c = ((Number) row[1]).longValue();
            if (h >= 0 && h < 24) buckets[h] += c;
        }

        List<HourlyPointDto> out = new ArrayList<>();
        for (int i = 0; i < 24; i++) out.add(new HourlyPointDto(i, buckets[i]));
        return out;
    }

    @Override
    public List<StoreActivityDto> storeActivity(LocalDate date) {
        Range r = dayRange(date);

        Map<Long, Long> created = toMap(auditRepo.countStoreListingActions(AuditAction.CREATE, r.start, r.end));
        Map<Long, Long> deleted = toMap(auditRepo.countStoreListingActions(AuditAction.DELETE, r.start, r.end));
        Map<Long, Long> inquiries = toMap(inquiryRepo.countByStore(r.start, r.end));
        Map<Long, Long> messages = toMap(msgRepo.countMessagesByStore(r.start, r.end));
        Map<Long, Long> unread = toMap(msgRepo.countUnreadByStore());

        Set<Long> storeIds = new LinkedHashSet<>();
        storeIds.addAll(created.keySet());
        storeIds.addAll(deleted.keySet());
        storeIds.addAll(inquiries.keySet());
        storeIds.addAll(messages.keySet());
        storeIds.addAll(unread.keySet());

        List<StoreActivityDto> out = new ArrayList<>();
        for (Long storeId : storeIds) {
            out.add(new StoreActivityDto(
                    storeId,
                    created.getOrDefault(storeId, 0L),
                    deleted.getOrDefault(storeId, 0L),
                    inquiries.getOrDefault(storeId, 0L),
                    messages.getOrDefault(storeId, 0L),
                    unread.getOrDefault(storeId, 0L)
            ));
        }

        out.sort(Comparator.comparingLong((StoreActivityDto s) ->
                (s.listingsCreated + s.listingsDeleted + s.inquiriesCreated + s.messagesSent)
        ).reversed());

        return out;
    }

    // ✅ YENİ: spam attempt yapan aktörler
    @Override
    public List<SpamAttemptActorDto> spamAttemptActors(LocalDate date) {
        Range r = dayRange(date);
        // "since" = gün başlangıcı
        return attemptRepo.topActorsSince(r.start).stream().map(row -> {
            var at  = (com.example.otoportdeneme.Enums.ActorType) row[0];
            Long aid = (row[1] == null) ? null : ((Number) row[1]).longValue();
            Long cnt = ((Number) row[2]).longValue();
            return new SpamAttemptActorDto(at.name(), aid, cnt);
        }).toList();
    }

    // -------- helpers --------

    private static class Range {
        Instant start;
        Instant end;
        Range(Instant s, Instant e) { start = s; end = e; }
    }

    private Range dayRange(LocalDate date) {
        ZoneId zone = ZoneId.of("Europe/Istanbul");
        ZonedDateTime z0 = date.atStartOfDay(zone);
        ZonedDateTime z1 = z0.plusDays(1);
        return new Range(z0.toInstant(), z1.toInstant());
    }

    private Map<Long, Long> toMap(List<Object[]> rows) {
        Map<Long, Long> m = new HashMap<>();
        for (Object[] r : rows) {
            Long k = ((Number) r[0]).longValue();
            Long v = ((Number) r[1]).longValue();
            m.put(k, v);
        }
        return m;
    }
}
