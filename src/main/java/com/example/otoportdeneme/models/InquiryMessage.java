package com.example.otoportdeneme.models;

import com.example.otoportdeneme.Enums.SenderType;
import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "inquiry_messages", indexes = {
        @Index(name = "ix_inquiry_msg_inquiry", columnList = "inquiry_id"),
        @Index(name = "ix_inquiry_msg_sentAt", columnList = "sentAt")
})
public class InquiryMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "inquiry_id", nullable = false)
    private Inquiry inquiry;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private SenderType senderType;

    // Store gönderiyorsa dolu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_sender_id")
    private Store storeSender;

    // Client gönderiyorsa dolu
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_sender_id")
    private Client clientSender;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false, updatable = false)
    private Instant sentAt = Instant.now();

    private Boolean readByStore = false;
    private Boolean readByClient = false;

    // -------- getters --------
    public Long getId() { return id; }

    public Inquiry getInquiry() { return inquiry; }

    public SenderType getSenderType() { return senderType; }

    public Store getStoreSender() { return storeSender; }

    public Client getClientSender() { return clientSender; }

    public String getContent() { return content; }

    public Instant getSentAt() { return sentAt; }

    public Boolean getReadByStore() { return readByStore; }

    public Boolean getReadByClient() { return readByClient; }

    // -------- setters --------
    public void setInquiry(Inquiry inquiry) { this.inquiry = inquiry; }

    public void setSenderType(SenderType senderType) { this.senderType = senderType; }

    public void setStoreSender(Store store) { this.storeSender = store; }

    public void setClientSender(Client client) { this.clientSender = client; }

    public void setContent(String content) { this.content = content; }

    public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }

    public void setReadByStore(boolean b) { this.readByStore = b; }

    public void setReadByClient(boolean b) { this.readByClient = b; }
}
