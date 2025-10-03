package com.example.covault.Tasks;

import com.example.covault.configs.SystemMessagesCache;
import com.example.covault.entities.ZZZSystemMessages;
import com.example.covault.repositories.ZZZSystemMessagesRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class SystemMessagesTask {

    private final ZZZSystemMessagesRepository systemMessagesRepository;
    private final SystemMessagesCache systemMessagesCache;

    @PostConstruct
    public void setup() {
        // Get all the contents of SystemMessages
        List<ZZZSystemMessages> systemMessagesList = systemMessagesRepository.findAll();

        // Save to cache
        systemMessagesCache.setMessages(systemMessagesList);

        // Log how many messages were cached
        log.info("SystemMessagesTask: Cached {} system messages", systemMessagesList.size());
    }
}