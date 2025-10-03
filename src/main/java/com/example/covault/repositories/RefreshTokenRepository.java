package com.example.covault.repositories;

import com.example.covault.entities.RefreshTokens;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokens, Long> {
    @Transactional
    void deleteByUserId(Long userId);
    
    Optional<RefreshTokens> findByTokenHash(String refreshToken);
}