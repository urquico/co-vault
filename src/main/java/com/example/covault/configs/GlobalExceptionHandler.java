package com.example.covault.configs;

import com.example.covault.dtos.APIResponse;
import com.example.covault.entities.ZZZSystemMessages;
import com.example.covault.exceptions.APIException;
import com.example.covault.utils.DBLogsUtility;
import com.example.covault.utils.ZZZCacheMessagesUtility;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;

@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler {
    private final ZZZCacheMessagesUtility systemMessageUtility;
    private final DBLogsUtility logger;

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse<Object>> handleAPIException(APIException ex, HttpServletResponse servletResponse) {
        // Prevent double-writing errors
        if (servletResponse.isCommitted()) {
            System.err.println("⚠️ Response already committed, skipping APIException write.");
            return null;
        }

        ZZZSystemMessages systemMessage = systemMessageUtility.getSystemMessageByKey(ex.getType());

        // Log error safely
        try {
            String[] callerInfo = logger.getCallerInfo(ex.getOriginStackTrace());
            logger.createErrorLogs(
                    systemMessage.getMessageType(),
                    callerInfo[0],
                    callerInfo[1] + " (line " + callerInfo[2] + ")",
                    ex.getMessage(),
                    Arrays.toString(Thread.currentThread().getStackTrace()),
                    ex.getEmail(),
                    ex.getMetadata()
            );
        } catch (Exception e) {
            System.err.println("Error logging failed: " + e.getMessage());
        }

        int status = systemMessage.getStatusCode();

        APIResponse<Object> response = new APIResponse<>(
                ex.getType(),
                null,
                null
        );

        return ResponseEntity.status(status).body(response);
    }
}
