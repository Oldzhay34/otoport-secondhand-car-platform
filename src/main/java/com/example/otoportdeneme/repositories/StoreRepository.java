package com.example.otoportdeneme.repositories;

import com.example.otoportdeneme.Interfaces.StoreCardView;
import com.example.otoportdeneme.Interfaces.StoreMyProfileView;
import com.example.otoportdeneme.Interfaces.StoreProfileView;
import com.example.otoportdeneme.models.Store;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoreRepository extends JpaRepository<Store, Long> {

    List<StoreCardView> findAllByOrderByIdDesc(Pageable pageable);

    Optional<StoreProfileView> findProjectedById(Long id);

    boolean existsByEmail(String mail);
    @Query("select s.id from Store s where s.email = :email")
    Optional<Long> findIdByEmail(@Param("email") String email);

    Optional<StoreMyProfileView> findMyProjectedById(Long id);


}
