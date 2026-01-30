package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.*;
import com.example.otoportdeneme.models.*;
import com.example.otoportdeneme.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryMessageRepository messageRepository;
    private final ListingRepository listingRepository;
    private final ClientRepository clientRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;

    private final MessageModerationService moderationService;
    private final MessageModerationAttemptService attemptService; // ✅

    public InquiryServiceImpl(
            InquiryRepository inquiryRepository,
            InquiryMessageRepository messageRepository,
            ListingRepository listingRepository,
            ClientRepository clientRepository,
            AuditService auditService,
            NotificationService notificationService,
            MessageModerationService moderationService,
            MessageModerationAttemptService attemptService
    ) {
        this.inquiryRepository = inquiryRepository;
        this.messageRepository = messageRepository;
        this.listingRepository = listingRepository;
        this.clientRepository = clientRepository;
        this.auditService = auditService;
        this.notificationService = notificationService;
        this.moderationService = moderationService;
        this.attemptService = attemptService;
    }

    // ================= GUEST =================
    @Override
    @Transactional
    public Long createInquiryGuest(Long listingId, String guestName, String guestEmail, String guestPhone,
                                   String firstMessage, String ip, String userAgent) {

        if (firstMessage == null || firstMessage.trim().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message required");

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        Inquiry inquiry = new Inquiry();
        inquiry.setListing(listing);
        inquiry.setStore(listing.getStore());
        inquiry.setGuestName(guestName);
        inquiry.setGuestEmail(guestEmail);
        inquiry.setGuestPhone(guestPhone);
        inquiry.setStatus(InquiryStatus.OPEN);
        inquiryRepository.save(inquiry);

        var res = moderationService.check(firstMessage.trim());
        if (!res.isAllowed()) {
            inquiry.setStatus(InquiryStatus.SPAM);
            inquiryRepository.save(inquiry);

            // ✅ attempt rollback yemesin
            attemptService.record(
                    ActorType.GUEST,
                    null,
                    inquiry.getId(),
                    res.getReason(),
                    res.getHitCount(),
                    res.getMatches(),
                    ip,
                    userAgent
            );

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Mesaj gönderilemedi (uygunsuz içerik tespit edildi)");
        }

        InquiryMessage msg = new InquiryMessage();
        msg.setInquiry(inquiry);
        msg.setSenderType(SenderType.GUEST);
        msg.setContent(firstMessage.trim());
        msg.setReadByStore(false);
        msg.setReadByClient(true);
        messageRepository.save(msg);

        notificationService.notify(
                listing.getStore(),
                NotificationType.NEW_MESSAGE,
                "Yeni mesaj (Guest)",
                "İlanınız için yeni bir mesaj aldınız.",
                null
        );

        return inquiry.getId();
    }

    // ================= CLIENT =================
    @Override
    @Transactional
    public Long createInquiryClient(Long listingId, Long clientId, String firstMessage, String ip, String userAgent) {

        if (firstMessage == null || firstMessage.trim().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message required");

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        Inquiry inquiry = new Inquiry();
        inquiry.setListing(listing);
        inquiry.setStore(listing.getStore());
        inquiry.setClient(client);
        inquiry.setStatus(InquiryStatus.OPEN);
        inquiryRepository.save(inquiry);

        var res = moderationService.check(firstMessage.trim());
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
                    userAgent
            );

            auditService.log(
                    client,
                    AuditAction.UPDATE,
                    "Inquiry",
                    inquiry.getId(),
                    "Blocked first message reason=" + res.getReason() + " matches=" + res.getMatches(),
                    ip,
                    userAgent
            );

            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Mesaj gönderilemedi (uygunsuz içerik tespit edildi)");
        }

        InquiryMessage msg = new InquiryMessage();
        msg.setInquiry(inquiry);
        msg.setSenderType(SenderType.CLIENT);
        msg.setClientSender(client);
        msg.setContent(firstMessage.trim());
        msg.setReadByStore(false);
        msg.setReadByClient(true);
        messageRepository.save(msg);

        notificationService.notify(
                listing.getStore(),
                NotificationType.NEW_MESSAGE,
                "Yeni mesaj",
                client.getFirstName() + " size mesaj gönderdi.",
                null
        );

        auditService.log(
                client,
                AuditAction.CREATE,
                "Inquiry",
                inquiry.getId(),
                "Client inquiry created",
                ip,
                userAgent
        );

        return inquiry.getId();
    }
}
