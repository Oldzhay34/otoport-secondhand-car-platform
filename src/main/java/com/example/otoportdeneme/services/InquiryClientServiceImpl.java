package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.*;
import com.example.otoportdeneme.dto_Objects.InquiryMessageDto;
import com.example.otoportdeneme.dto_Response.InquiryUpsertResponse;
import com.example.otoportdeneme.models.*;
import com.example.otoportdeneme.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InquiryClientServiceImpl implements InquiryClientService {

    private final InquiryRepository inquiryRepository;
    private final InquiryMessageRepository messageRepository;
    private final ClientRepository clientRepository;
    private final ListingRepository listingRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;

    private final MessageModerationService moderationService;
    private final MessageModerationAttemptService attemptService; // ✅

    public InquiryClientServiceImpl(
            InquiryRepository inquiryRepository,
            InquiryMessageRepository messageRepository,
            ClientRepository clientRepository,
            ListingRepository listingRepository,
            AuditService auditService,
            NotificationService notificationService,
            MessageModerationService moderationService,
            MessageModerationAttemptService attemptService
    ) {
        this.inquiryRepository = inquiryRepository;
        this.messageRepository = messageRepository;
        this.clientRepository = clientRepository;
        this.listingRepository = listingRepository;
        this.auditService = auditService;
        this.notificationService = notificationService;
        this.moderationService = moderationService;
        this.attemptService = attemptService;
    }

    @Override
    @Transactional
    public InquiryUpsertResponse upsert(Long clientId, Long listingId, String message, String ip, String ua) {

        if (clientId == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");

        if (listingId == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "listingId required");

        if (message == null || message.trim().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message required");

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Client not found"));

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        Store store = listing.getStore();
        if (store == null)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Listing has no store");

        Inquiry inquiry = inquiryRepository.findByListingIdAndClientId(listingId, clientId)
                .orElseGet(() -> {
                    Inquiry i = new Inquiry();
                    i.setListing(listing);
                    i.setStore(store);
                    i.setClient(client);
                    i.setStatus(InquiryStatus.OPEN);
                    return inquiryRepository.save(i);
                });

        var res = moderationService.check(message.trim());
        if (!res.isAllowed()) {
            inquiry.setStatus(InquiryStatus.SPAM);
            inquiryRepository.save(inquiry);

            // ✅ attempt rollback yemesin
            attemptService.record(
                    ActorType.CLIENT,
                    clientId,
                    inquiry.getId(),
                    res.getReason(),
                    res.getHitCount(),
                    res.getMatches(),
                    ip,
                    ua
            );

            auditService.log(
                    client,
                    AuditAction.UPDATE,
                    "Inquiry",
                    inquiry.getId(),
                    "Blocked message attempt reason=" + res.getReason() + " matches=" + res.getMatches(),
                    ip,
                    ua
            );

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Mesaj gönderilemedi (uygunsuz içerik tespit edildi)");
        }

        InquiryMessage msg = new InquiryMessage();
        msg.setInquiry(inquiry);
        msg.setSenderType(SenderType.CLIENT);
        msg.setClientSender(client);
        msg.setContent(message.trim());
        msg.setReadByClient(true);
        msg.setReadByStore(false);
        messageRepository.save(msg);

        auditService.log(
                client,
                AuditAction.CREATE,
                "InquiryMessage",
                msg.getId(),
                "Client sent inquiry message",
                ip,
                ua
        );

        Inquiry full = inquiryRepository.findByIdFetchAll(inquiry.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquiry not found"));

        return map(full);
    }

    @Override
    @Transactional
    public InquiryUpsertResponse getThread(Long clientId, Long listingId) {
        if (clientId == null)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");

        Inquiry inq = inquiryRepository.findByListingIdAndClientIdFetchAll(listingId, clientId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquiry not found"));

        return map(inq);
    }

    private InquiryUpsertResponse map(Inquiry inquiry) {
        InquiryUpsertResponse res = new InquiryUpsertResponse();
        res.setInquiryId(inquiry.getId());
        res.setListingId(inquiry.getListing().getId());
        res.setStoreId(inquiry.getStore().getId());
        res.setStatus(inquiry.getStatus().name());
        res.setCreatedAt(inquiry.getCreatedAt());

        res.setGuestName(null);
        res.setGuestEmail(null);
        res.setGuestPhone(null);

        if (inquiry.getClient() != null) {
            res.setClientEmail(inquiry.getClient().getEmail());
        }

        List<InquiryMessageDto> msgs = messageRepository.findByInquiryIdOrderBySentAtAsc(inquiry.getId())
                .stream()
                .map(m -> {
                    InquiryMessageDto d = new InquiryMessageDto();
                    d.setId(m.getId());
                    d.setSenderType(m.getSenderType().name());
                    d.setContent(m.getContent());
                    d.setSentAt(m.getSentAt());
                    return d;
                })
                .toList();

        res.setMessages(msgs);
        return res;
    }
}
