package com.example.otoportdeneme.dto_Response;

import com.example.otoportdeneme.dto_Objects.InquiryArchivedMessageDto;

import java.time.Instant;
import java.util.List;

public class InquiryArchiveChunkResponse {

    private Long inquiryId;
    private Integer chunkNo;

    private Instant fromSentAt;
    private Instant toSentAt;

    private Integer messageCount;
    private List<InquiryArchivedMessageDto> messages;

    public InquiryArchiveChunkResponse() {}

    public InquiryArchiveChunkResponse(Long inquiryId,
                                       Integer chunkNo,
                                       Instant fromSentAt,
                                       Instant toSentAt,
                                       Integer messageCount,
                                       List<InquiryArchivedMessageDto> messages) {
        this.inquiryId = inquiryId;
        this.chunkNo = chunkNo;
        this.fromSentAt = fromSentAt;
        this.toSentAt = toSentAt;
        this.messageCount = messageCount;
        this.messages = messages;
    }

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

    public List<InquiryArchivedMessageDto> getMessages() { return messages; }
    public void setMessages(List<InquiryArchivedMessageDto> messages) { this.messages = messages; }
}
