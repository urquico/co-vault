package com.example.covault.utils;

import com.example.covault.entities.ZZZErrorLogs;
import com.example.covault.enums.MessageType;
import com.example.covault.repositories.ZZZErrorLogsRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

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
                log.setExtraData(objectMapper.writeValueAsString(sanitizeExtraData(extraData)));
            } catch (JsonProcessingException e) {
                ObjectNode errorNode = objectMapper.createObjectNode();
                errorNode.put("serializationError", e.getMessage());
                try {
                    log.setExtraData(objectMapper.writeValueAsString(errorNode));
                } catch (JsonProcessingException ignored) {
                    log.setExtraData("{\"serializationError\":\"unserializable data\"}");
                }
            }
        }

        errorLogsRepository.save(log);
    }

    private Object sanitizeExtraData(Object extraData) {
        if (extraData instanceof Exception ex) {
            // Convert exception into JSON-friendly structure
            return Map.of(
                    "type", ex.getClass().getName(),
                    "message", ex.getMessage(),
                    "cause", ex.getCause() != null ? ex.getCause().toString() : null,
                    "stackTrace", Arrays.stream(ex.getStackTrace())
                            .map(StackTraceElement::toString)
                            .limit(10) // avoid giant logs
                            .toList()
            );
        }
        if (extraData instanceof Map<?, ?> map) {
            // Clean nested exceptions inside maps
            Map<String, Object> cleaned = new LinkedHashMap<>();
            map.forEach((k, v) -> cleaned.put(String.valueOf(k), sanitizeExtraData(v)));
            return cleaned;
        }
        return extraData;
    }

    public String[] getCallerInfo() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

        boolean foundApiResponse = false;

        for (StackTraceElement element : stackTrace) {
            String className = element.getClassName();

            // Skip until after ApiResponse constructor
            if (className.contains("ApiResponse")) {
                foundApiResponse = true;
                continue;
            }

            if (foundApiResponse) {
                // First frame after ApiResponse is the real caller
                return new String[]{
                        element.getClassName(),
                        element.getMethodName(),
                        String.valueOf(element.getLineNumber())
                };
            }
        }

        return new String[]{"UnknownClass", "UnknownMethod", "-1"};
    }
}