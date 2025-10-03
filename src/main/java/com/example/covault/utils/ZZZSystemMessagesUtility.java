package com.example.covault.utils;

import com.example.covault.configs.SystemMessagesCache;
import com.example.covault.entities.ZZZSystemMessages;
import com.example.covault.enums.SystemMessageType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ZZZSystemMessagesUtility {

    private final SystemMessagesCache systemMessagesCache;

    public ZZZSystemMessages getMessageByKey(SystemMessageType key) {
        return systemMessagesCache.getMessages()
                .stream()
                .filter(m -> m.getMessageKey().equals(key.toString()))
                .findFirst()
                .orElse(null);
    }
}