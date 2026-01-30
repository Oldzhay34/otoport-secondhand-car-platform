package com.example.otoportdeneme.services;

import com.example.otoportdeneme.Enums.AccountStatus;
import com.example.otoportdeneme.Interfaces.ClientStatusView;
import com.example.otoportdeneme.dto_Objects.admin.ClientStatusDto;
import com.example.otoportdeneme.models.Client;
import com.example.otoportdeneme.repositories.ClientRepository;
import com.example.otoportdeneme.services.AdminAuditLogService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ClientStatusServiceImpl implements ClientStatusService {

    private final ClientRepository clientRepository;
    private final AdminAuditLogService audit;

    public ClientStatusServiceImpl(ClientRepository clientRepository, AdminAuditLogService audit) {
        this.clientRepository = clientRepository;
        this.audit = audit;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClientStatusDto> listAllClients() {
        List<ClientStatusView> rows = clientRepository.findAllClientStatus();
        return rows.stream()
                .map(v -> new ClientStatusDto(
                        v.getId(),
                        v.getFirstName(),
                        v.getLastName(),
                        v.getEmail(),
                        v.getStatus()
                ))
                .toList();
    }

    @Override
    @Transactional
    public ClientStatusDto setClientStatus(Long clientId, AccountStatus desired) {
        Client c = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("Client not found: " + clientId));

        AccountStatus current = c.getStatus();

        AccountStatus next = (desired == null)
                ? (current == AccountStatus.ACTIVE ? AccountStatus.SUSPENDED : AccountStatus.ACTIVE)
                : desired;

        c.setStatus(next);
        clientRepository.save(c);

        audit.write(
                "CLIENT_STATUS_UPDATE",
                "clientId=" + c.getId() +
                        ", email=" + c.getEmail() +
                        ", from=" + current +
                        ", to=" + next
        );

        return new ClientStatusDto(c.getId(), c.getFirstName(), c.getLastName(), c.getEmail(), c.getStatus());
    }

    @Override
    @Transactional
    public List<ClientStatusDto> bulkSetStatus(List<Long> clientIds, AccountStatus status) {
        if (clientIds == null || clientIds.isEmpty()) return List.of();
        if (status == null) throw new IllegalArgumentException("Bulk status cannot be null");

        List<Client> clients = clientRepository.findAllById(clientIds);
        if (clients.size() != clientIds.size()) {
            throw new EntityNotFoundException("Some clientIds not found");
        }

        for (Client c : clients) {
            AccountStatus from = c.getStatus();
            c.setStatus(status);

            audit.write(
                    "CLIENT_STATUS_BULK_UPDATE",
                    "clientId=" + c.getId() +
                            ", email=" + c.getEmail() +
                            ", from=" + from +
                            ", to=" + status
            );
        }

        clientRepository.saveAll(clients);

        return clients.stream()
                .map(c -> new ClientStatusDto(c.getId(), c.getFirstName(), c.getLastName(), c.getEmail(), c.getStatus()))
                .toList();
    }
}
