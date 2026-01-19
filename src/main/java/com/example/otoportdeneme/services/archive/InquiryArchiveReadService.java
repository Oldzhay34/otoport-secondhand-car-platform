package com.example.otoportdeneme.services.archive;

import com.example.otoportdeneme.dto_Objects.InquiryArchivedMessageDto;
import com.example.otoportdeneme.dto_Response.InquiryArchiveChunkMetaDto;
import com.example.otoportdeneme.dto_Response.InquiryArchiveChunkResponse;
import com.example.otoportdeneme.dto_Response.InquiryArchiveChunksResponse;
import com.example.otoportdeneme.models.InquiryMessageArchiveChunk;
import com.example.otoportdeneme.repositories.InquiryMessageArchiveChunkRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class InquiryArchiveReadService {

    private final InquiryMessageArchiveChunkRepository chunkRepository;

    public InquiryArchiveReadService(InquiryMessageArchiveChunkRepository chunkRepository) {
        this.chunkRepository = chunkRepository;
    }

    public InquiryArchiveChunksResponse listChunks(Long inquiryId) {
        List<InquiryMessageArchiveChunk> chunks = chunkRepository.findByInquiryIdOrderByChunkNoAsc(inquiryId);

        List<InquiryArchiveChunkMetaDto> meta = chunks.stream()
                .map(c -> new InquiryArchiveChunkMetaDto(
                        c.getChunkNo(),
                        c.getFromSentAt(),
                        c.getToSentAt(),
                        c.getMessageCount()
                ))
                .toList();

        return new InquiryArchiveChunksResponse(inquiryId, meta);
    }

    public InquiryArchiveChunkResponse getChunk(Long inquiryId, Integer chunkNo) {
        InquiryMessageArchiveChunk chunk = chunkRepository.findByInquiryIdAndChunkNo(inquiryId, chunkNo)
                .orElseThrow(() -> new IllegalArgumentException("Archive chunk not found"));

        // JSONL gzip -> jsonl string
        String jsonl = ArchiveCodec.decodeToJsonl(chunk.getPayloadCompressed());

        List<InquiryArchivedMessageDto> messages = parseJsonl(jsonl);

        return new InquiryArchiveChunkResponse(
                inquiryId,
                chunkNo,
                chunk.getFromSentAt(),
                chunk.getToSentAt(),
                chunk.getMessageCount(),
                messages
        );
    }

    /**
     * Satır formatı:
     * {"t":"2026-01-16T10:10:10Z","s":"C","sid":123,"c":"Merhaba"}
     * Burada minimum parser yapıyoruz (Jackson şart değil).
     */
    private List<InquiryArchivedMessageDto> parseJsonl(String jsonl) {
        List<InquiryArchivedMessageDto> out = new ArrayList<>();
        if (jsonl == null || jsonl.isBlank()) return out;

        String[] lines = jsonl.split("\n");
        for (String line : lines) {
            line = line.trim();
            if (line.isEmpty()) continue;

            // mini-safe parse (alanlar sabit)
            String t = readJsonString(line, "\"t\":\"", "\"");
            String s = readJsonString(line, "\"s\":\"", "\"");
            String sidRaw = readJsonNumberOrNull(line, "\"sid\":", ",");
            String c = readJsonString(line, "\"c\":\"", "\"}");

            Instant sentAt = null;
            try { sentAt = (t == null || t.isBlank()) ? null : Instant.parse(t); }
            catch (Exception ignored) {}

            Long senderId = null;
            try { senderId = (sidRaw == null) ? null : Long.valueOf(sidRaw); }
            catch (Exception ignored) {}

            String sender = "STORE";
            if ("C".equalsIgnoreCase(s)) sender = "CLIENT";

            out.add(new InquiryArchivedMessageDto(sentAt, sender, senderId, unescapeJson(c)));
        }
        return out;
    }

    private String readJsonString(String line, String start, String end) {
        int i = line.indexOf(start);
        if (i < 0) return null;
        i += start.length();
        int j = line.indexOf(end, i);
        if (j < 0) return null;
        return line.substring(i, j);
    }

    private String readJsonNumberOrNull(String line, String start, String endDelim) {
        int i = line.indexOf(start);
        if (i < 0) return null;
        i += start.length();

        int j = line.indexOf(endDelim, i);
        if (j < 0) j = line.indexOf("}", i);
        if (j < 0) return null;

        String raw = line.substring(i, j).trim();
        if ("null".equalsIgnoreCase(raw)) return null;
        // raw "123"
        return raw.replaceAll("[^0-9]", "");
    }

    private String unescapeJson(String s) {
        if (s == null) return null;
        return s.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }
}
