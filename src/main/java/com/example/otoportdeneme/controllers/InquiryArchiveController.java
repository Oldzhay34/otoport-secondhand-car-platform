package com.example.otoportdeneme.controllers;

import com.example.otoportdeneme.dto_Response.InquiryArchiveChunkResponse;
import com.example.otoportdeneme.dto_Response.InquiryArchiveChunksResponse;
import com.example.otoportdeneme.services.archive.InquiryArchiveReadService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/inquiries")
public class InquiryArchiveController {

    private final InquiryArchiveReadService archiveReadService;

    public InquiryArchiveController(InquiryArchiveReadService archiveReadService) {
        this.archiveReadService = archiveReadService;
    }

    @GetMapping("/{inquiryId}/archive/chunks")
    public InquiryArchiveChunksResponse listChunks(@PathVariable Long inquiryId) {
        return archiveReadService.listChunks(inquiryId);
    }

    @GetMapping("/{inquiryId}/archive/chunks/{chunkNo}")
    public InquiryArchiveChunkResponse getChunk(@PathVariable Long inquiryId,
                                                @PathVariable Integer chunkNo) {
        return archiveReadService.getChunk(inquiryId, chunkNo);
    }
}
