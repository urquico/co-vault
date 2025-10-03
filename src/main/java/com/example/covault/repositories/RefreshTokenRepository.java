package com.example.covault.repositories;

import com.example.covault.entities.RefreshTokens;
import com.example.covault.entities.RefreshTokensId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokens, Long> {

    Optional<RefreshTokens> findByTokenHash(String refreshToken);

    Optional<RefreshTokens> findById(RefreshTokensId refreshTokensId);

    Optional<RefreshTokens> findById_UserIdAndId_Device(Long id, String macbook);

    @Transactional
    void deleteById_UserIdAndId_Device(Long userId, String macbook);
}