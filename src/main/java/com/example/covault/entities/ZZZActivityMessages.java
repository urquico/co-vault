package com.example.covault.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "activity_messages")
public class ZZZActivityMessages {

    @Id
    @Column(name = "message_key", length = 100, nullable = false, unique = true)
    private String messageKey;

    @Column(name = "message_text", length = 500, nullable = false)
    private String messageText;
}