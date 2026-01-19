package com.example.otoportdeneme.dto_Response;

import java.time.Instant;

public class InquiryArchiveChunkMetaDto {

    private Integer chunkNo;
    private Instant fromSentAt;
    private Instant toSentAt;
    private Integer messageCount;

    public InquiryArchiveChunkMetaDto() {}

    public InquiryArchiveChunkMetaDto(Integer chunkNo, Instant fromSentAt, Instant toSentAt, Integer messageCount) {
        this.chunkNo = chunkNo;
        this.fromSentAt = fromSentAt;
        this.toSentAt = toSentAt;
        this.messageCount = messageCount;
    }

    public Integer getChunkNo() { return chunkNo; }
    public void setChunkNo(Integer chunkNo) { this.chunkNo = chunkNo; }

    public Instant getFromSentAt() { return fromSentAt; }
    public void setFromSentAt(Instant fromSentAt) { this.fromSentAt = fromSentAt; }

    public Instant getToSentAt() { return toSentAt; }
    public void setToSentAt(Instant toSentAt) { this.toSentAt = toSentAt; }

    public Integer getMessageCount() { return messageCount; }
    public void setMessageCount(Integer messageCount) { this.messageCount = messageCount; }
}
