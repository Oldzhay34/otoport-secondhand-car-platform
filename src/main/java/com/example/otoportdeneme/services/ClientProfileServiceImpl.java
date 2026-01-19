package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Interfaces.ClientProfileView;
import com.example.otoportdeneme.repositories.ClientRepository;
import com.example.otoportdeneme.repositories.GuestAccessLogRepository;
import jakarta.transaction.Transactional;
import com.example.otoportdeneme.models.GuestAccessLog;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClientProfileServiceImpl implements ClientProfileService {

    private final ClientRepository clientRepository;
    private final GuestAccessLogRepository guestAccessLogRepository;

    public ClientProfileServiceImpl(
            ClientRepository clientRepository,
            GuestAccessLogRepository guestAccessLogRepository
    ) {
        this.clientRepository = clientRepository;
        this.guestAccessLogRepository = guestAccessLogRepository;
    }

    @Override
    @Transactional
    public Optional<ClientProfileView> getMyProfile(
            Long clientId,
            String ip,
            String userAgent
    ) {
        // eğer guest profile görüntülersee null ile karşılaşır - register a zorla
        if (clientId == null) {
            GuestAccessLog log = new GuestAccessLog();
            log.setAccessType("PROFILE");
            log.setTarget("client-profile");
            log.setIpAddress(ip);
            log.setUserAgent(userAgent);
            guestAccessLogRepository.save(log);

            // frontend bunu görünce register ekranı açar
            return Optional.empty();
        }

        // ✅ Logged-in client → profil döndür
        return clientRepository.findProjectedById(clientId);
    }
}
