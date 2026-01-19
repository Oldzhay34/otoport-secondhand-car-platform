package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Interfaces.ClientProfileView;
import com.example.otoportdeneme.repositories.ClientRepository;
import jakarta.transaction.Transactional;
import com.example.otoportdeneme.models.Client;
import org.springframework.stereotype.Service;

@Service
public class ClientProfileUpdateServiceImpl implements ClientProfileUpdateService {

    private final ClientRepository clientRepository;

    public ClientProfileUpdateServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientProfileView getMyProfileOrThrow(Long clientId) {
        return clientRepository.findProjectedById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));
    }

    @Override
    @Transactional
    public ClientProfileView updateMyProfile(
            Long clientId,
            String firstName,
            String lastName,
            String phone,
            java.time.LocalDate birthDate,
            Boolean marketingConsent
    ) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found"));

        //e mail kesinlikle değiştirilemez şifre is sunucu lazım !
        if (firstName != null && !firstName.isBlank()) client.setFirstName(firstName);
        if (lastName != null && !lastName.isBlank()) client.setLastName(lastName);


        if (phone != null){
            client.setPhone(phone);
        }

        client.setBirthDate(birthDate);
        if (marketingConsent != null) client.setMarketingConsent(marketingConsent);

        clientRepository.save(client);
        return getMyProfileOrThrow(clientId);
    }
}
