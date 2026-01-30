package com.example.otoportdeneme.models;

import com.example.otoportdeneme.Enums.ActorType;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name="message_moderation_attempts", indexes = {
        @Index(name="ix_mma_created", columnList="createdAt"),
        @Index(name="ix_mma_actor", columnList="actorType,actorId"),
        @Index(name="ix_mma_inquiry", columnList="inquiryId")
})
public class MessageModerationAttempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=20)
    private ActorType actorType;   // CLIENT / STORE / GUEST

    private Long actorId;          // guest null

    @Column(nullable=false)
    private Long inquiryId;

    @Column(nullable=false, length=40)
    private String reason;         // PROFANITY / SPAM_LINK / SPAM ...

    @Column(nullable=false)
    private Integer hitCount;

    @Column(length=500)
    private String matchedPreview; // eşleşen kelimeler özet

    @Column(length=45)
    private String ipAddress;

    @Column(length=255)
    private String userAgent;

    @Column(nullable=false, updatable=false)
    private Instant createdAt = Instant.now();

    // getters/setters
    public Long getId() { return id; }

    public ActorType getActorType() { return actorType; }
    public void setActorType(ActorType actorType) { this.actorType = actorType; }

    public Long getActorId() { return actorId; }
    public void setActorId(Long actorId) { this.actorId = actorId; }

    public Long getInquiryId() { return inquiryId; }
    public void setInquiryId(Long inquiryId) { this.inquiryId = inquiryId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Integer getHitCount() { return hitCount; }
    public void setHitCount(Integer hitCount) { this.hitCount = hitCount; }

    public String getMatchedPreview() { return matchedPreview; }
    public void setMatchedPreview(String matchedPreview) { this.matchedPreview = matchedPreview; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public Instant getCreatedAt() { return createdAt; }
}
