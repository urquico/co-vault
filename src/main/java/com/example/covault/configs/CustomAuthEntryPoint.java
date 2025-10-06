package com.example.covault.configs;

import com.example.covault.dtos.APIResponse;
import com.example.covault.enums.SystemMessageType;
import com.example.covault.utils.ZZZSystemMessagesUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ZZZSystemMessagesUtility systemMessagesUtility;

    public CustomAuthEntryPoint(ZZZSystemMessagesUtility systemMessagesUtility) {
        this.systemMessagesUtility = systemMessagesUtility;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        APIResponse<Void> apiResponse = new APIResponse<>(
                SystemMessageType.UNAUTHORIZED,
                null,
                systemMessagesUtility
        );

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }
}