package com.example.covault.utils;

import com.example.covault.entities.ZZZErrorLogs;
import com.example.covault.enums.MessageType;
import com.example.covault.repositories.ZZZErrorLogsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DBLogsUtility {

    private final ZZZErrorLogsRepository errorLogsRepository;

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
        if (extraData != null) log.setExtraData(extraData.toString());

        errorLogsRepository.save(log);
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