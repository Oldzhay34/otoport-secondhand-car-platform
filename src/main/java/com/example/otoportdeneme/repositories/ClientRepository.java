package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.Interfaces.ClientProfileView;
import com.example.otoportdeneme.Interfaces.ClientStatusView;
import com.example.otoportdeneme.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    Optional<ClientProfileView> findProjectedById(Long id);

    Optional<Client> findByEmail(String email);

    @Query("select c.id from Client c where c.email = :email")
    Optional<Long> findIdByEmail(@Param("email") String email);

    // ✅ status + temel bilgiler
    @Query("""
        select c.id as id,
               c.firstName as firstName,
               c.lastName as lastName,
               c.email as email,
               c.status as status
        from Client c
        order by c.id desc
    """)
    List<ClientStatusView> findAllClientStatus();

    @Query("""
        select c.id as id,
               c.firstName as firstName,
               c.lastName as lastName,
               c.email as email,
               c.status as status
        from Client c
        where c.id in :ids
    """)
    List<ClientStatusView> findClientStatusByIds(@Param("ids") List<Long> ids);


}

