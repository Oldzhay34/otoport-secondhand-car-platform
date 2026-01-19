package com.example.otoportdeneme.services.archive;

import com.example.otoportdeneme.models.InquiryMessage;
import com.example.otoportdeneme.models.InquiryMessageArchiveChunk;
import com.example.otoportdeneme.repositories.InquiryMessageArchiveChunkRepository;
import com.example.otoportdeneme.repositories.InquiryMessageRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class InquiryArchiveService {

    private final InquiryMessageRepository messageRepository;
    private final InquiryMessageArchiveChunkRepository chunkRepository;

    public InquiryArchiveService(InquiryMessageRepository messageRepository,
                                 InquiryMessageArchiveChunkRepository chunkRepository) {
        this.messageRepository = messageRepository;
        this.chunkRepository = chunkRepository;
    }

    /**
     * @param before bu tarihten eski mesajları arşivler
     * @param chunkSize ör. 200
     */
    @Transactional
    public int archiveOlderThan(Instant before, int chunkSize) {
        if (chunkSize <= 0) chunkSize = 200;

        List<Long> inquiryIds = messageRepository.findInquiryIdsHavingMessagesBefore(before);
        int totalArchivedMessages = 0;

        for (Long inquiryId : inquiryIds) {
            totalArchivedMessages += archiveInquiryOldMessages(inquiryId, before, chunkSize);
        }
        return totalArchivedMessages;
    }

    @Transactional
    public int archiveInquiryOldMessages(Long inquiryId, Instant before, int chunkSize) {
        List<InquiryMessage> old = messageRepository.findOldMessagesForInquiry(inquiryId, before);
        if (old.isEmpty()) return 0;

        int chunkNoStart = (int) chunkRepository.countByInquiryId(inquiryId); // basit yaklaşım
        int archivedCount = 0;

        List<List<InquiryMessage>> chunks = split(old, chunkSize);

        int chunkNo = chunkNoStart;
        for (List<InquiryMessage> part : chunks) {
            Instant from = part.get(0).getSentAt();
            Instant to = part.get(part.size() - 1).getSentAt();

            byte[] payload = ArchiveCodec.encodeJsonlGzip(part);

            InquiryMessageArchiveChunk chunk = new InquiryMessageArchiveChunk();
            chunk.setInquiryId(inquiryId);
            chunk.setChunkNo(chunkNo++);
            chunk.setFromSentAt(from);
            chunk.setToSentAt(to);
            chunk.setMessageCount(part.size());
            chunk.setPayloadFormat("JSONL_GZIP_V1");
            chunk.setPayloadCompressed(payload);

            chunkRepository.save(chunk);
            archivedCount += part.size();
        }

        // ✅ chunk’lar yazıldı -> şimdi aktif tablodan sil
        messageRepository.deleteOldMessagesForInquiry(inquiryId, before);

        return archivedCount;
    }

    private static <T> List<List<T>> split(List<T> list, int size) {
        List<List<T>> out = new ArrayList<>();
        for (int i = 0; i < list.size(); i += size) {
            out.add(list.subList(i, Math.min(i + size, list.size())));
        }
        return out;
    }
}
