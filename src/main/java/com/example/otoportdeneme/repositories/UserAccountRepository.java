package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.models.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findByEmail(String email);
    boolean existsByEmail(String email);
    @Query("select u.id from UserAccount u where u.email = :email")
    Optional<Long> findIdByEmail(String email);
}
