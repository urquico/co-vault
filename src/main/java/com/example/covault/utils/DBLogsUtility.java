package com.example.covault.utils;

import com.example.covault.entities.ZZZActivityLogs;
import com.example.covault.entities.ZZZErrorLogs;
import com.example.covault.enums.CoVaultTableType;
import com.example.covault.enums.MessageType;
import com.example.covault.repositories.UserRepository;
import com.example.covault.repositories.ZZZActivityLogsRepository;
import com.example.covault.repositories.ZZZErrorLogsRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class DBLogsUtility {

    private final ZZZErrorLogsRepository errorLogsRepository;
    private final ZZZActivityLogsRepository activityLogsRepository;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;

    public void createActivityLogs(String activity, String oldData, String newData) {
        createActivityLogs(activity, null, null, oldData, newData);
    }

    public void createActivityLogs(
            String activity,
            CoVaultTableType referenceTable,
            Long referenceId,
            String oldData,
            String newData
    ) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();

        String email = (String) request.getAttribute("email");
        String ipAddress = (String) request.getAttribute("ipAddress");
        String userAgent = (String) request.getAttribute("userAgent");

        ZZZActivityLogs log = new ZZZActivityLogs();

        log.setEmail(email);
        log.setIpAddress(ipAddress);
        log.setActivity(activity);
        log.setUserAgent(userAgent);
        log.setReferenceTable(referenceTable);
        log.setReferenceId(referenceId);
        log.setOldData(oldData);
        log.setNewData(newData);
        log.setTimestamp(LocalDateTime.now());

        activityLogsRepository.save(log);
    }

    public void createErrorLogs(
            MessageType errorType,
            String className,
            String methodName,
            String message,
            String stackTrace,
            Object extraData
    ) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
                .currentRequestAttributes()).getRequest();

        String email = request.getAttribute("email").toString();

        ZZZErrorLogs log = new ZZZErrorLogs();
        log.setTimestamp(LocalDateTime.now());
        log.setErrorType(errorType);
        log.setClassName(className);
        log.setMethodName(methodName);
        log.setMessage(message);
        log.setStackTrace(stackTrace);
        log.setEmail(email);

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