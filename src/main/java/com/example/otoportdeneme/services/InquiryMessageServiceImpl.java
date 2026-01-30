package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.*;
import com.example.otoportdeneme.models.*;
import com.example.otoportdeneme.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InquiryMessageServiceImpl implements InquiryMessageService {

    private final InquiryRepository inquiryRepository;
    private final InquiryMessageRepository messageRepository;
    private final ClientRepository clientRepository;
    private final StoreRepository storeRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;
    private final MessageModerationService moderationService;
    private final MessageModerationAttemptRepository attemptRepo;

    public InquiryMessageServiceImpl(
            InquiryRepository inquiryRepository,
            InquiryMessageRepository messageRepository,
            ClientRepository clientRepository,
            StoreRepository storeRepository,
            AuditService auditService,
            NotificationService notificationService,
            MessageModerationService moderationService,
            MessageModerationAttemptRepository attemptRepo
    ) {
        this.inquiryRepository = inquiryRepository;
        this.messageRepository = messageRepository;
        this.clientRepository = clientRepository;
        this.storeRepository = storeRepository;
        this.auditService = auditService;
        this.notificationService = notificationService;
        this.moderationService = moderationService;
        this.attemptRepo = attemptRepo;
    }

    @Override
    @Transactional
    public void replyAsStore(Long inquiryId, Long storeId, String message, String ip, String userAgent) {

        if (message == null || message.trim().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message required");

        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("Inquiry not found"));

        if (!inquiry.getStore().getId().equals(storeId)) {
            throw new IllegalStateException("Store not authorized");
        }

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        // ✅ MODERATION: kaydetmeden önce kontrol
        guardMessageOrThrow(inquiry, ActorType.STORE, storeId, message, ip, userAgent);

        InquiryMessage msg = new InquiryMessage();
        msg.setInquiry(inquiry);
        msg.setSenderType(SenderType.STORE);
        msg.setStoreSender(store);
        msg.setContent(message.trim());
        msg.setReadByStore(true);
        msg.setReadByClient(false);
        messageRepository.save(msg);

        if (inquiry.getClient() != null) {
            notificationService.notify(
                    inquiry.getClient(),
                    NotificationType.NEW_MESSAGE,
                    "Mağazadan yanıt",
                    "İlan için mesajınıza yanıt geldi.",
                    null
            );
        }

        auditService.log(
                store,
                AuditAction.CREATE,
                "InquiryMessage",
                msg.getId(),
                "Store replied to inquiry",
                ip,
                userAgent
        );
    }

    @Override
    @Transactional
    public void replyAsClient(Long inquiryId, Long clientId, String message, String ip, String userAgent) {

        if (message == null || message.trim().isEmpty())
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message required");

        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("Inquiry not found"));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        if (inquiry.getClient() == null || !inquiry.getClient().getId().equals(clientId)) {
            throw new IllegalStateException("Client not authorized");
        }

        // ✅ MODERATION: kaydetmeden önce kontrol
        guardMessageOrThrow(inquiry, ActorType.CLIENT, clientId, message, ip, userAgent);

        InquiryMessage msg = new InquiryMessage();
        msg.setInquiry(inquiry);
        msg.setSenderType(SenderType.CLIENT);
        msg.setClientSender(client);
        msg.setContent(message.trim());
        msg.setReadByStore(false);
        msg.setReadByClient(true);
        messageRepository.save(msg);

        notificationService.notify(
                inquiry.getStore(),
                NotificationType.NEW_MESSAGE,
                "Yeni mesaj",
                "Bir müşteri mesaj gönderdi.",
                null
        );

        auditService.log(
                client,
                AuditAction.CREATE,
                "InquiryMessage",
                msg.getId(),
                "Client replied to inquiry",
                ip,
                userAgent
        );
    }

    @Override
    @Transactional
    public void markReadByStore(Long inquiryId) {
        messageRepository.findByInquiryIdOrderBySentAtAsc(inquiryId)
                .forEach(m -> m.setReadByStore(true));
    }

    @Override
    @Transactional
    public void markReadByClient(Long inquiryId) {
        messageRepository.findByInquiryIdOrderBySentAtAsc(inquiryId)
                .forEach(m -> m.setReadByClient(true));
    }

    private void guardMessageOrThrow(Inquiry inquiry,
                                     ActorType actorType,
                                     Long actorId,
                                     String message,
                                     String ip,
                                     String ua) {

        var res = moderationService.check(message);

        if (res.isAllowed()) return;

        // inquiry spam işaretle
        inquiry.setStatus(InquiryStatus.SPAM);
        inquiryRepository.save(inquiry);

        // attempt kaydı
        MessageModerationAttempt a = new MessageModerationAttempt();
        a.setActorType(actorType);
        a.setActorId(actorId);
        a.setInquiryId(inquiry.getId());
        a.setReason(res.getReason());
        a.setHitCount(res.getHitCount());
        a.setMatchedPreview(String.join(",", res.getMatches() == null ? List.of() : res.getMatches()));
        a.setIpAddress(ip);
        a.setUserAgent(ua);
        attemptRepo.save(a);

        // audit (store veya client)
        Object actor = (actorType == ActorType.STORE) ? inquiry.getStore() : inquiry.getClient();
        if (actor instanceof Store s) {
            auditService.log(
                    s,
                    AuditAction.UPDATE,
                    "Inquiry",
                    inquiry.getId(),
                    "Blocked message attempt reason=" + res.getReason() + " matches=" + res.getMatches(),
                    ip,
                    ua
            );
        } else if (actor instanceof Client c) {
            auditService.log(
                    c,
                    AuditAction.UPDATE,
                    "Inquiry",
                    inquiry.getId(),
                    "Blocked message attempt reason=" + res.getReason() + " matches=" + res.getMatches(),
                    ip,
                    ua
            );
        }

        throw new ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "Mesaj gönderilemedi (uygunsuz içerik tespit edildi)"
        );
    }
}
