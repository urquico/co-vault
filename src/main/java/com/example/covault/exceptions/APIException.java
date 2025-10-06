package com.example.covault.exceptions;

import com.example.covault.entities.ZZZSystemMessages;
import com.example.covault.enums.SystemMessageType;
import com.example.covault.utils.ZZZSystemMessagesUtility;
import lombok.Getter;

@Getter
public class APIException extends RuntimeException {
    private final SystemMessageType type;
    private final Long userId;
    private final Object metadata;
    private final ZZZSystemMessagesUtility systemMessageUtility;

    public APIException(SystemMessageType type, Long userId, Object metadata, ZZZSystemMessagesUtility systemMessageUtility) {
        ZZZSystemMessages systemMessage = systemMessageUtility.getMessageByKey(type);

        super(systemMessage.getMessageText());
        this.type = type;
        this.userId = userId;
        this.metadata = metadata;
        this.systemMessageUtility = systemMessageUtility;
    }
}
