package com.example.covault.configs;

import com.example.covault.entities.ZZZSystemMessages;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@Setter
public class SystemMessagesCache {
    private List<ZZZSystemMessages> messages;
}