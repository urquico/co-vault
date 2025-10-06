package com.example.covault.utils;

import com.example.covault.entities.ZZZErrorLogs;
import com.example.covault.enums.MessageType;
import com.example.covault.repositories.ZZZErrorLogsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DBLogsUtility {

    private final ZZZErrorLogsRepository errorLogsRepository;
    private final ObjectMapper objectMapper;

    public void createErrorLogs(
            MessageType errorType,
            String className,
            String methodName,
            String message,
            String stackTrace,
            Long userId,
            Object extraData
    ) {
        ZZZErrorLogs log = new ZZZErrorLogs();
        log.setTimestamp(LocalDateTime.now());
        log.setErrorType(errorType);
        log.setClassName(className);
        log.setMethodName(methodName);
        log.setMessage(message);
        log.setStackTrace(stackTrace);
        log.setUserId(userId);

        if (extraData != null) {
            try {
                log.setExtraData(objectMapper.writeValueAsString(extraData));
            } catch (Exception e) {
                log.setExtraData("{\"serializationError\":\"" + e.getMessage() + "\"}");
            }
        }

        errorLogsRepository.save(log);
    }

    public String[] getCallerInfo(StackTraceElement[] stackTrace) {
        boolean foundController = false;

        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();

            // Skip framework & exception-related classes
            if (className.startsWith("java.") ||
                    className.startsWith("org.springframework.") ||
                    className.contains("GlobalExceptionHandler") ||
                    className.contains("APIException")) {
                continue;
            }

            // First non-framework class is likely your controller/service
            return new String[]{
                    element.getClassName(),
                    element.getMethodName(),
                    String.valueOf(element.getLineNumber())
            };
        }

        return new String[]{"UnknownClass", "UnknownMethod", "-1"};
    }

}