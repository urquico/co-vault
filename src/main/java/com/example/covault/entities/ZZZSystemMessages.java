package com.example.covault.entities;

import com.example.covault.enums.MessageType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "system_messages")
public class ZZZSystemMessages {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "message_key", length = 100, nullable = false, unique = true)
    private String messageKey;

    @Column(name = "locale", length = 10, nullable = false)
    private String locale;

    @Column(name = "message_text", length = 500, nullable = false)
    private String messageText;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", length = 20)
    private MessageType messageType;
}
