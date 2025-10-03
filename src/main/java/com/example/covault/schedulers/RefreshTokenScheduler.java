package com.example.covault.schedulers;

import com.example.covault.repositories.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
@RequiredArgsConstructor
public class RefreshTokenScheduler {
    @Value("${spring.app.task.refreshTokenCleanUp}")
    private boolean isRefreshTokenCleanupEnabled;

    private final RefreshTokenRepository refreshTokenRepository;

    @Scheduled(cron = "0 0 4 * * *") // every 4am
    @Transactional
    public void init() {
        if (!isRefreshTokenCleanupEnabled) return;
        deleteExpiredRefreshTokens();
    }

    @Transactional
    private void deleteExpiredRefreshTokens() {
        // Get all expired refresh token
        Instant now = Instant.now();

        // Delete all
        refreshTokenRepository.deleteAllExpiredTokens(now);
    }
}