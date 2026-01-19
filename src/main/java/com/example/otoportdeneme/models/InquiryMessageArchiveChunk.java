package com.example.otoportdeneme.models;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "inquiry_message_archive_chunks", indexes = {
        @Index(name = "ix_arch_inquiry_chunk", columnList = "inquiryId,chunkNo"),
        @Index(name = "ix_arch_range", columnList = "inquiryId,fromSentAt,toSentAt")
})
public class InquiryMessageArchiveChunk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long inquiryId;

    @Column(nullable = false)
    private Integer chunkNo;

    @Column(nullable = false)
    private Instant fromSentAt;

    @Column(nullable = false)
    private Instant toSentAt;

    @Column(nullable = false)
    private Integer messageCount;

    @Column(nullable = false, length = 30)
    private String payloadFormat = "JSONL_GZIP_V1";

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(nullable = false)
    private byte[] payloadCompressed;

    @Column(nullable = false, updatable = false)
    private Instant archivedAt = Instant.now();

    // getters/setters

    public Long getId() { return id; }

    public Long getInquiryId() { return inquiryId; }
    public void setInquiryId(Long inquiryId) { this.inquiryId = inquiryId; }

    public Integer getChunkNo() { return chunkNo; }
    public void setChunkNo(Integer chunkNo) { this.chunkNo = chunkNo; }

    public Instant getFromSentAt() { return fromSentAt; }
    public void setFromSentAt(Instant fromSentAt) { this.fromSentAt = fromSentAt; }

    public Instant getToSentAt() { return toSentAt; }
    public void setToSentAt(Instant toSentAt) { this.toSentAt = toSentAt; }

    public Integer getMessageCount() { return messageCount; }
    public void setMessageCount(Integer messageCount) { this.messageCount = messageCount; }

    public String getPayloadFormat() { return payloadFormat; }
    public void setPayloadFormat(String payloadFormat) { this.payloadFormat = payloadFormat; }

    public byte[] getPayloadCompressed() { return payloadCompressed; }
    public void setPayloadCompressed(byte[] payloadCompressed) { this.payloadCompressed = payloadCompressed; }

    public Instant getArchivedAt() { return archivedAt; }
}
