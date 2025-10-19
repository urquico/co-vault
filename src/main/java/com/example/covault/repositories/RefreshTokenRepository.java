package com.example.covault.repositories;

import com.example.covault.entities.RefreshTokens;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokens, Long> {

    Optional<RefreshTokens> findByTokenHash(String refreshToken);
    
    @Transactional
    void deleteById_EmailAndId_Device(String email, String macbook);

    @Modifying
    @Query(value = "DELETE FROM refresh_tokens WHERE expires_at < :time", nativeQuery = true)
    void deleteAllExpiredTokens(@Param("time") Instant time);

    Optional<RefreshTokens> findById_EmailAndId_Device(String email, String device);
}