package com.example.covault.dtos;

import com.example.covault.enums.SystemMessageType;
import com.example.covault.utils.ZZZSystemMessagesUtility;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class ApiResponse<T> {
    private String message;
    private int status;
    private T data;
    private String timeStamp = Instant.now().toString();

    public ApiResponse(SystemMessageType systemMessageType, ZZZSystemMessagesUtility systemMessagesUtility, T data) {
        this.message = systemMessagesUtility.getMessageByKey(systemMessageType).getMessageText();
        this.status = systemMessagesUtility.getMessageByKey(systemMessageType).getStatusCode();
        this.data = data;
    }
}