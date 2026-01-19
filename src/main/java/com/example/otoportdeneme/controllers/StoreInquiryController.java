package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Objects.InquiryMessageDto;
import com.example.otoportdeneme.dto_Objects.InquiryThreadItemDto;
import com.example.otoportdeneme.dto_Requests.InquiryReplyRequest;
import com.example.otoportdeneme.dto_Response.StoreInquiryListResponse;
import com.example.otoportdeneme.dto_Response.StoreInquiryThreadResponse;
import com.example.otoportdeneme.dto_Response.UnreadCountResponse;
import com.example.otoportdeneme.models.Inquiry;
import com.example.otoportdeneme.models.InquiryMessage;
import com.example.otoportdeneme.repositories.InquiryMessageRepository;
import com.example.otoportdeneme.repositories.InquiryRepository;
import com.example.otoportdeneme.repositories.StoreRepository;
import com.example.otoportdeneme.services.InquiryMessageService;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Comparator;
import java.util.List;

@RestController
@RequestMapping("/api/store/inquiries")
public class StoreInquiryController {

    private final InquiryRepository inquiryRepository;
    private final InquiryMessageRepository messageRepository;
    private final StoreRepository storeRepository;
    private final InquiryMessageService inquiryMessageService;

    public StoreInquiryController(InquiryRepository inquiryRepository,
                                  InquiryMessageRepository messageRepository,
                                  StoreRepository storeRepository,
                                  InquiryMessageService inquiryMessageService) {
        this.inquiryRepository = inquiryRepository;
        this.messageRepository = messageRepository;
        this.storeRepository = storeRepository;
        this.inquiryMessageService = inquiryMessageService;
    }

    // ✅ inbox list
    @GetMapping
    public StoreInquiryListResponse list(Authentication auth,
                                         @RequestParam(value = "q", required = false) String q) {
        Long storeId = resolveStoreId(auth);

        List<Inquiry> inquiries = inquiryRepository.findByStoreIdOrderByCreatedAtDesc(storeId);

        // Basit arama (listing title / guest / client email) - DTO mapping sırasında filtreleyelim
        String needle = (q == null) ? null : q.trim().toLowerCase();

        List<InquiryThreadItemDto> items = inquiries.stream()
                .map(inq -> toThreadItemDto(inq))
                .filter(dto -> {
                    if (needle == null || needle.isEmpty()) return true;
                    return (dto.getListingTitle() != null && dto.getListingTitle().toLowerCase().contains(needle))
                            || (dto.getClientName() != null && dto.getClientName().toLowerCase().contains(needle))
                            || (dto.getClientEmail() != null && dto.getClientEmail().toLowerCase().contains(needle))
                            || (dto.getGuestName() != null && dto.getGuestName().toLowerCase().contains(needle))
                            || (dto.getGuestEmail() != null && dto.getGuestEmail().toLowerCase().contains(needle));
                })
                // son mesaja göre sırala (yoksa createdAt)
                .sorted(Comparator.comparing(InquiryThreadItemDto::getLastSentAt,
                        Comparator.nullsLast(Comparator.naturalOrder())).reversed())
                .toList();

        return new StoreInquiryListResponse(items);
    }

    // ✅ thread detail
    @GetMapping("/{inquiryId}")
    public StoreInquiryThreadResponse get(Authentication auth, @PathVariable Long inquiryId) {
        Long storeId = resolveStoreId(auth);

        Inquiry inq = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("Inquiry not found"));

        if (!inq.getStore().getId().equals(storeId)) {
            throw new IllegalArgumentException("Forbidden");
        }

        List<InquiryMessage> msgs = messageRepository.findByInquiryIdOrderBySentAtAsc(inquiryId);

        StoreInquiryThreadResponse res = new StoreInquiryThreadResponse();
        res.setInquiryId(inq.getId());
        res.setListingId(inq.getListing() != null ? inq.getListing().getId() : null);
        res.setListingTitle(inq.getListing() != null ? inq.getListing().getTitle() : "İlan");

        res.setStatus(inq.getStatus() != null ? inq.getStatus().name() : null);
        res.setCreatedAt(inq.getCreatedAt());

        if (inq.getClient() != null) {
            // projendeki Client alanlarına göre uyarlayabilirsin
            res.setClientEmail(inq.getClient().getEmail());
            // res.setClientName(inq.getClient().getFullName());
        } else {
            res.setGuestName(inq.getGuestName());
            res.setGuestEmail(inq.getGuestEmail());
            res.setGuestPhone(inq.getGuestPhone());
        }

        List<InquiryMessageDto> msgDtos = msgs.stream().map(m -> {
            InquiryMessageDto d = new InquiryMessageDto();
            d.setId(m.getId());
            d.setSenderType(m.getSenderType() != null ? m.getSenderType().name() : null);
            d.setContent(m.getContent());
            d.setSentAt(m.getSentAt());
            return d;
        }).toList();

        res.setMessages(msgDtos);
        return res;
    }

    // ✅ store reply
    @PostMapping("/{inquiryId}/reply")
    public void reply(Authentication auth,
                      @PathVariable Long inquiryId,
                      @RequestBody InquiryReplyRequest req,
                      @RequestHeader(value = "X-Forwarded-For", required = false) String ip,
                      @RequestHeader(value = "User-Agent", required = false) String ua) {

        Long storeId = resolveStoreId(auth);

        String msg = req != null ? req.getMessage() : null;
        if (msg == null || msg.trim().isEmpty()) {
            throw new IllegalArgumentException("message is required");
        }

        inquiryMessageService.replyAsStore(
                inquiryId,
                storeId,
                msg.trim(),
                ip,
                ua
        );
    }

    // ✅ thread açılınca okundu
    @PatchMapping("/{inquiryId}/read")
    public void markRead(Authentication auth, @PathVariable Long inquiryId) {
        Long storeId = resolveStoreId(auth);

        Inquiry inq = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("Inquiry not found"));

        if (!inq.getStore().getId().equals(storeId)) {
            throw new IllegalArgumentException("Forbidden");
        }

        inquiryMessageService.markReadByStore(inquiryId);
    }

    // ✅ home badge: toplam unread inquiry mesaj sayısı
    @GetMapping("/unread-count")
    public UnreadCountResponse unreadCount(Authentication auth) {
        Long storeId = resolveStoreId(auth);

        // store'a ait inquiry'lerin id listesi
        List<Inquiry> inquiries = inquiryRepository.findByStoreIdOrderByCreatedAtDesc(storeId);

        long total = 0;
        for (Inquiry inq : inquiries) {
            // store'un okumadığı mesaj sayısı (bu inquiry'de)
            total += messageRepository.countByInquiryIdAndReadByStoreFalse(inq.getId());
        }

        return new UnreadCountResponse(total);
    }

    // ---- helpers ----

    private InquiryThreadItemDto toThreadItemDto(Inquiry inq) {
        InquiryThreadItemDto d = new InquiryThreadItemDto();
        d.setInquiryId(inq.getId());

        if (inq.getListing() != null) {
            d.setListingId(inq.getListing().getId());
            d.setListingTitle(inq.getListing().getTitle());
        } else {
            d.setListingTitle("İlan");
        }

        if (inq.getStatus() != null) d.setStatus(inq.getStatus().name());
        d.setCreatedAt(inq.getCreatedAt());

        if (inq.getClient() != null) {
            d.setClientEmail(inq.getClient().getEmail());
            // d.setClientName(inq.getClient().getFullName());
        } else {
            d.setGuestName(inq.getGuestName());
            d.setGuestEmail(inq.getGuestEmail());
            d.setGuestPhone(inq.getGuestPhone());
        }

        // unread sayısı
        long unread = messageRepository.countByInquiryIdAndReadByStoreFalse(inq.getId());
        d.setUnreadCount(unread);

        // last message (aktif mesaj tablosundan)
        List<InquiryMessage> msgs = messageRepository.findByInquiryIdOrderBySentAtAsc(inq.getId());
        if (!msgs.isEmpty()) {
            InquiryMessage last = msgs.get(msgs.size() - 1);
            d.setLastMessage(last.getContent());
            d.setLastSentAt(last.getSentAt());
        }

        return d;
    }

    private Long resolveStoreId(Authentication auth) {
        if (auth == null) throw new IllegalArgumentException("Unauthorized");
        String email = auth.getName();
        return storeRepository.findIdByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));
    }
}
