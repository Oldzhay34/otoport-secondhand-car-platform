package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.Interfaces.ClientProfileView;
import com.example.otoportdeneme.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {
    Optional<ClientProfileView> findProjectedById(Long id);
    // Client entity User'dan inherit ediyorsa email alanı User tablosunda olabilir, bu yine çalışır.
    Optional<Client> findByEmail(String email);

    @Query("select c.id from Client c where c.email = :email")
    Optional<Long> findIdByEmail(@Param("email") String email);
}
