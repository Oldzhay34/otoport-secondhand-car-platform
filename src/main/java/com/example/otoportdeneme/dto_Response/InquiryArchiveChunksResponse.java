package com.example.otoportdeneme.dto_Response;

import java.util.List;

public class InquiryArchiveChunksResponse {
    private Long inquiryId;
    private List<InquiryArchiveChunkMetaDto> chunks;

    public InquiryArchiveChunksResponse() {}

    public InquiryArchiveChunksResponse(Long inquiryId, List<InquiryArchiveChunkMetaDto> chunks) {
        this.inquiryId = inquiryId;
        this.chunks = chunks;
    }

    public Long getInquiryId() { return inquiryId; }
    public void setInquiryId(Long inquiryId) { this.inquiryId = inquiryId; }

    public List<InquiryArchiveChunkMetaDto> getChunks() { return chunks; }
    public void setChunks(List<InquiryArchiveChunkMetaDto> chunks) { this.chunks = chunks; }
}
