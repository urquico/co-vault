package com.example.covault.entities;

import com.example.covault.enums.MessageType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "error_logs")
public class ZZZErrorLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Enumerated(EnumType.STRING)
    @Column(name = "error_type", length = 20)
    private MessageType errorType;

    @Column(length = 255)
    private String className;

    @Column(length = 255)
    private String methodName;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(columnDefinition = "LONGTEXT")
    private String stackTrace;

    @Column
    private String email;

    @Column(columnDefinition = "JSON")
    private String extraData;
}