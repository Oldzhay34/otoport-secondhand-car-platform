package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.InquiryStatus;
import com.example.otoportdeneme.Enums.SenderType;
import com.example.otoportdeneme.dto_Objects.InquiryMessageDto;
import com.example.otoportdeneme.dto_Requests.InquiryUpsertRequest;
import com.example.otoportdeneme.dto_Response.InquiryUpsertResponse;
import com.example.otoportdeneme.models.*;
import com.example.otoportdeneme.repositories.*;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class InquiryPublicServiceImpl implements InquiryPublicService {

    private final InquiryRepository inquiryRepository;
    private final InquiryMessageRepository messageRepository;
    private final ListingRepository listingRepository;
    private final StoreRepository storeRepository;
    private final ClientRepository clientRepository;

    public InquiryPublicServiceImpl(
            InquiryRepository inquiryRepository,
            InquiryMessageRepository messageRepository,
            ListingRepository listingRepository,
            StoreRepository storeRepository,
            ClientRepository clientRepository
    ) {
        this.inquiryRepository = inquiryRepository;
        this.messageRepository = messageRepository;
        this.listingRepository = listingRepository;
        this.storeRepository = storeRepository;
        this.clientRepository = clientRepository;
    }

    @Override
    @Transactional
    public InquiryUpsertResponse upsert(InquiryUpsertRequest req, String authEmailOrNull, String ip, String userAgent) {
        if (req == null || req.getListingId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "listingId required");
        }
        String msg = req.getMessage() == null ? null : req.getMessage().trim();
        if (msg == null || msg.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message required");
        }

        Listing listing = listingRepository.findById(req.getListingId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Listing not found"));

        Store store = listing.getStore();
        if (store == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Listing store missing");

        Client client;
        if (authEmailOrNull != null && !authEmailOrNull.isBlank()) {
            client = clientRepository.findByEmail(authEmailOrNull).orElse(null);
        } else {
            client = null;
        }

        // --- inquiry seç / oluştur ---
        Inquiry inquiry;

        if (client != null) {
            inquiry = inquiryRepository.findByListingIdAndClientId(listing.getId(), client.getId())
                    .orElseGet(() -> {
                        Inquiry i = new Inquiry();
                        i.setListing(listing);
                        i.setStore(store);
                        i.setClient(client);
                        i.setStatus(InquiryStatus.OPEN);
                        return inquiryRepository.save(i);
                    });
        } else {
            // guest
            String guestEmail = norm(req.getGuestEmail());
            if (guestEmail == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "guestEmail required for guest");
            }

            inquiry = inquiryRepository.findByListingIdAndGuestEmailIgnoreCase(listing.getId(), guestEmail)
                    .orElseGet(() -> {
                        Inquiry i = new Inquiry();
                        i.setListing(listing);
                        i.setStore(store);
                        i.setGuestName(safeTrim(req.getGuestName(), 80));
                        i.setGuestEmail(safeTrim(guestEmail, 190));
                        i.setGuestPhone(safeTrim(req.getGuestPhone(), 30));
                        i.setStatus(InquiryStatus.OPEN);
                        return inquiryRepository.save(i);
                    });
        }

        // --- mesaj ekle ---
        InquiryMessage im = new InquiryMessage();
        im.setInquiry(inquiry);

        if (client != null) {
            im.setSenderType(SenderType.CLIENT);
            im.setClientSender(client);
            im.setReadByClient(true);
            im.setReadByStore(false);
        } else {
            // guest’i de CLIENT gibi treat ediyoruz (UI tarafında “client” bubble gözüksün)
            im.setSenderType(SenderType.CLIENT);
            im.setReadByClient(true);
            im.setReadByStore(false);
        }

        im.setContent(msg);
        messageRepository.save(im);

        return mapThread(inquiry.getId());
    }

    @Override
    public InquiryUpsertResponse getThread(Long inquiryId, String authEmailOrNull) {
        Inquiry inq = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquiry not found"));

        // basit yetki: client login ise kendi inquiry’si olmalı
        if (authEmailOrNull != null && !authEmailOrNull.isBlank()) {
            Client client = clientRepository.findByEmail(authEmailOrNull).orElse(null);
            if (client != null) {
                if (inq.getClient() == null || !inq.getClient().getId().equals(client.getId())) {
                    throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
                }
            }
        }
        return mapThread(inquiryId);
    }

    @Override
    public InquiryUpsertResponse getThreadByListing(Long listingId, String authEmailOrNull) {
        Client client = null;
        if (authEmailOrNull != null && !authEmailOrNull.isBlank()) {
            client = clientRepository.findByEmail(authEmailOrNull).orElse(null);
        }

        Inquiry inq;
        if (client != null) {
            inq = inquiryRepository.findByListingIdAndClientId(listingId, client.getId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquiry not found"));
        } else {
            // guest için listing bazlı otomatik get etmiyoruz (email bilmeden güvenli değil)
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Login required");
        }

        return mapThread(inq.getId());
    }

    @Override
    @Transactional
    public InquiryUpsertResponse reply(Long inquiryId, String message, String authEmailOrNull, String guestEmailOrNull, String ip, String userAgent) {
        String msg = message == null ? null : message.trim();
        if (msg == null || msg.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "message required");
        }

        Inquiry inq = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquiry not found"));

        Client client = null;
        if (authEmailOrNull != null && !authEmailOrNull.isBlank()) {
            client = clientRepository.findByEmail(authEmailOrNull).orElse(null);
        }

        // yetki kontrolü
        if (client != null) {
            if (inq.getClient() == null || !inq.getClient().getId().equals(client.getId())) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
            }
        } else {
            // guest: inquiry’nin guestEmail’i ile gelen guestEmail eşleşmeli
            String ge = norm(guestEmailOrNull);
            if (ge == null || inq.getGuestEmail() == null || !inq.getGuestEmail().equalsIgnoreCase(ge)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Forbidden");
            }
        }

        InquiryMessage im = new InquiryMessage();
        im.setInquiry(inq);

        if (client != null) {
            im.setSenderType(SenderType.CLIENT);
            im.setClientSender(client);
        } else {
            im.setSenderType(SenderType.CLIENT);
        }

        im.setContent(msg);
        im.setReadByClient(true);
        im.setReadByStore(false);

        messageRepository.save(im);

        return mapThread(inquiryId);
    }

    // ---------------- helpers ----------------

    private InquiryUpsertResponse mapThread(Long inquiryId) {
        Inquiry inq = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inquiry not found"));

        List<InquiryMessage> msgs = messageRepository.findByInquiryIdOrderBySentAtAsc(inquiryId);

        InquiryUpsertResponse res = new InquiryUpsertResponse();
        res.setInquiryId(inq.getId());
        res.setListingId(inq.getListing() != null ? inq.getListing().getId() : null);
        res.setStoreId(inq.getStore() != null ? inq.getStore().getId() : null);
        res.setStatus(inq.getStatus() != null ? inq.getStatus().name() : null);
        res.setCreatedAt(inq.getCreatedAt());

        if (inq.getClient() != null) res.setClientEmail(inq.getClient().getEmail());
        res.setGuestName(inq.getGuestName());
        res.setGuestEmail(inq.getGuestEmail());
        res.setGuestPhone(inq.getGuestPhone());

        List<InquiryMessageDto> dtoList = msgs.stream().map(m -> {
            InquiryMessageDto d = new InquiryMessageDto();
            d.setId(m.getId());
            d.setSenderType(m.getSenderType() != null ? m.getSenderType().name() : null);
            d.setContent(m.getContent());
            d.setSentAt(m.getSentAt());
            return d;
        }).toList();

        res.setMessages(dtoList);
        return res;
    }

    private static String norm(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }

    private static String safeTrim(String s, int maxLen) {
        String t = norm(s);
        if (t == null) return null;
        return t.length() <= maxLen ? t : t.substring(0, maxLen);
    }
}
