package com.example.covault.services;

import com.example.covault.dtos.APIResponse;
import com.example.covault.dtos.auth.LoginRequestDto;
import com.example.covault.entities.RefreshTokens;
import com.example.covault.entities.RefreshTokensId;
import com.example.covault.entities.Users;
import com.example.covault.enums.ActivityType;
import com.example.covault.enums.SystemMessageType;
import com.example.covault.exceptions.APIException;
import com.example.covault.repositories.RefreshTokenRepository;
import com.example.covault.repositories.UserRepository;
import com.example.covault.utils.CookieUtility;
import com.example.covault.utils.JWTUtility;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

@RequiredArgsConstructor
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTUtility jwtUtility;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CookieUtility cookieUtility;

    public APIResponse<Void> login(
            LoginRequestDto req,
            String device,
            HttpServletResponse response
    ) {

        Users user = userRepository.findByEmail(req.getEmail()).orElse(null);
        if (user == null)
            throw new APIException(
                    SystemMessageType.USER_NOT_FOUND,
                    Map.of("req", req, "device", device)
            );

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword()))
            throw new APIException(
                    SystemMessageType.INCORRECT_PASSWORD,
                    Map.of("req", req, "device", device)
            );

        String accessToken = jwtUtility.generateAccessToken(user.getEmail());
        String refreshToken = jwtUtility.generateRefreshToken();

        // Find existing token
        RefreshTokens refreshTokens = refreshTokenRepository.findById_EmailAndId_Device(user.getEmail(), device).orElse(null);
        Instant now = Instant.now();

        // Upsert token
        if (refreshTokens == null) {
            refreshTokens = new RefreshTokens();
            RefreshTokensId refreshTokenId = new RefreshTokensId();

            refreshTokenId.setEmail(user.getEmail());
            refreshTokenId.setDevice(device);

            refreshTokens.setId(refreshTokenId);
            refreshTokens.setCreatedAt(now);
        }

        refreshTokens.setTokenHash(refreshToken);
        refreshTokens.setExpiresAt(now.plus(1, ChronoUnit.DAYS));
        refreshTokens.setUpdatedAt(now);

        refreshTokenRepository.save(refreshTokens);

        ResponseCookie accessCookie = cookieUtility.createHttpOnlyCookie("access_token", accessToken, 15 * 60);
        ResponseCookie refreshCookie = cookieUtility.createHttpOnlyCookie("refresh_token", refreshToken, 1 * 24 * 60 * 60);

        response.addHeader(HttpHeaders.SET_COOKIE, accessCookie.toString());
        response.addHeader(HttpHeaders.SET_COOKIE, refreshCookie.toString());

        return new APIResponse<>(SystemMessageType.SUCCESS, null, ActivityType.LOGIN);
    }
}
