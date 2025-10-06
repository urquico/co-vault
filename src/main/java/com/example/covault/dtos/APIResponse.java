package com.example.covault.dtos;

import com.example.covault.entities.ZZZSystemMessages;
import com.example.covault.enums.SystemMessageType;
import com.example.covault.utils.ZZZSystemMessagesUtility;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
public class APIResponse<T> {
    private String message;
    private int status;
    private T data;
    private String timeStamp = Instant.now().toString();

    // Success Response
    public APIResponse(SystemMessageType systemMessageType, T data, ZZZSystemMessagesUtility systemMessagesUtility) {
        ZZZSystemMessages systemMessage = systemMessagesUtility.getMessageByKey(systemMessageType);

        this.message = systemMessage.getMessageText();
        this.status = systemMessage.getStatusCode();
        this.data = data;
    }
}