package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.AuditAction;
import com.example.otoportdeneme.Enums.NotificationType;
import com.example.otoportdeneme.Enums.SenderType;
import com.example.otoportdeneme.repositories.*;
import jakarta.transaction.Transactional;
import com.example.otoportdeneme.models.*;
import org.springframework.stereotype.Service;

@Service
public class InquiryMessageServiceImpl implements InquiryMessageService {

    private final InquiryRepository inquiryRepository;
    private final InquiryMessageRepository messageRepository;
    private final ClientRepository clientRepository;
    private final StoreRepository storeRepository;
    private final AuditService auditService;
    private final NotificationService notificationService;

    public InquiryMessageServiceImpl(
            InquiryRepository inquiryRepository,
            InquiryMessageRepository messageRepository,
            ClientRepository clientRepository,
            StoreRepository storeRepository,
            AuditService auditService,
            NotificationService notificationService
    ) {
        this.inquiryRepository = inquiryRepository;
        this.messageRepository = messageRepository;
        this.clientRepository = clientRepository;
        this.storeRepository = storeRepository;
        this.auditService = auditService;
        this.notificationService = notificationService;
    }

    // store un message lara cevabı

    @Override
    @Transactional
    public void replyAsStore(
            Long inquiryId,
            Long storeId,
            String message,
            String ip,
            String userAgent
    ) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("Inquiry not found"));

        if (!inquiry.getStore().getId().equals(storeId)) {
            throw new IllegalStateException("Store not authorized");
        }

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new IllegalArgumentException("Store not found"));

        InquiryMessage msg = new InquiryMessage();
        msg.setInquiry(inquiry);
        msg.setSenderType(SenderType.STORE);
        msg.setStoreSender(store);
        msg.setContent(message);
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

    //client ın bu message lara cevabı

    @Override
    @Transactional
    public void replyAsClient(
            Long inquiryId,
            Long clientId,
            String message,
            String ip,
            String userAgent
    ) {
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new IllegalArgumentException("Inquiry not found"));

        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        if (inquiry.getClient() == null ||
                !inquiry.getClient().getId().equals(clientId)) {
            throw new IllegalStateException("Client not authorized");
        }

        InquiryMessage msg = new InquiryMessage();
        msg.setInquiry(inquiry);
        msg.setSenderType(SenderType.CLIENT);
        msg.setClientSender(client);
        msg.setContent(message);
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

    // client eğer bu mesaageları okursa

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
}
