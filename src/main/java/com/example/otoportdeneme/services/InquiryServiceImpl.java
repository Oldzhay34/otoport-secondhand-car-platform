package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.AuditAction;
import com.example.otoportdeneme.Enums.NotificationType;
import com.example.otoportdeneme.Enums.SenderType;
import com.example.otoportdeneme.repositories.*;
import jakarta.transaction.Transactional;
import com.example.otoportdeneme.models.*;
import org.springframework.stereotype.Service;

@Service
public class InquiryServiceImpl implements InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryMessageRepository messageRepository;
    private final ListingRepository listingRepository;
    private final ClientRepository clientRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;

    public InquiryServiceImpl(
            InquiryRepository inquiryRepository,
            InquiryMessageRepository messageRepository,
            ListingRepository listingRepository,
            ClientRepository clientRepository,
            AuditService auditService,
            NotificationService notificationService
    ) {
        this.inquiryRepository = inquiryRepository;
        this.messageRepository = messageRepository;
        this.listingRepository = listingRepository;
        this.clientRepository = clientRepository;
        this.auditService = auditService;
        this.notificationService = notificationService;
    }

    // ================= GUEST =================

    @Override
    @Transactional
    public Long createInquiryGuest(
            Long listingId,
            String guestName,
            String guestEmail,
            String guestPhone,
            String firstMessage,
            String ip,
            String userAgent
    ) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        Inquiry inquiry = new Inquiry();
        inquiry.setListing(listing);
        inquiry.setStore(listing.getStore());
        inquiry.setGuestName(guestName);
        inquiry.setGuestEmail(guestEmail);
        inquiry.setGuestPhone(guestPhone);
        inquiryRepository.save(inquiry);

        InquiryMessage msg = new InquiryMessage();
        msg.setInquiry(inquiry);
        msg.setSenderType(SenderType.GUEST);
        msg.setContent(firstMessage);
        msg.setReadByStore(false);
        msg.setReadByClient(true);
        messageRepository.save(msg);

        // 🔔 Store bildirimi
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
    public Long createInquiryClient(
            Long listingId,
            Long clientId,
            String firstMessage,
            String ip,
            String userAgent
    ) {
        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new IllegalArgumentException("Listing not found"));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        Inquiry inquiry = new Inquiry();
        inquiry.setListing(listing);
        inquiry.setStore(listing.getStore());
        inquiry.setClient(client);
        inquiryRepository.save(inquiry);

        InquiryMessage msg = new InquiryMessage();
        msg.setInquiry(inquiry);
        msg.setSenderType(SenderType.CLIENT);
        msg.setClientSender(client);
        msg.setContent(firstMessage);
        msg.setReadByStore(false);
        msg.setReadByClient(true);
        messageRepository.save(msg);

        // 🔔 Store bildirimi
        notificationService.notify(
                listing.getStore(),
                NotificationType.NEW_MESSAGE,
                "Yeni mesaj",
                client.getFirstName() + " size mesaj gönderdi.",
                null
        );

        // 🧾 Audit
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
