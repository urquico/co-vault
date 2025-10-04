package com.example.covault.dtos;

import com.example.covault.enums.MessageType;
import com.example.covault.enums.SystemMessageType;
import com.example.covault.utils.DBLogsUtility;
import com.example.covault.utils.ZZZSystemMessagesUtility;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.Arrays;

@Data
@NoArgsConstructor
public class ApiResponse<T> {
    private String message;
    private int status;
    private T data;
    private String timeStamp = Instant.now().toString();

    @Setter
    private static DBLogsUtility logger;

    public ApiResponse(SystemMessageType systemMessageType, ZZZSystemMessagesUtility systemMessagesUtility, T data) {
        this.message = systemMessagesUtility.getMessageByKey(systemMessageType).getMessageText();
        this.status = systemMessagesUtility.getMessageByKey(systemMessageType).getStatusCode();
        this.data = data;

        // Check for error-like status
        if (status >= 300 && status <= 599 && logger != null) {
            try {
                String[] callerInfo = logger.getCallerInfo();
                String className = callerInfo[0];
                String methodName = callerInfo[1];
                String lineNumber = callerInfo[2];

                logger.createErrorLogs(
                        MessageType.ERROR,
                        className,
                        methodName + " (line " + lineNumber + ")",
                        message,
                        Arrays.toString(Thread.currentThread().getStackTrace()),
                        null,
                        null
                );
            } catch (Exception e) {
                System.err.println("Error logging failed: " + e.getMessage());
            }
        }
    }
}