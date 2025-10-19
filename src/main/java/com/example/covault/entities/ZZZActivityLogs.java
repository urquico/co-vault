package com.example.covault.entities;

import com.example.covault.enums.CoVaultTableType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "activity_logs")
public class ZZZActivityLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "email")
    private String email;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @Column(nullable = false, length = 255)
    private String activity;

    @Column(name = "user_agent", length = 512)
    private String userAgent;

    @Enumerated(EnumType.STRING)
    @Column(name = "reference_table", length = 50)
    private CoVaultTableType referenceTable;

    @Column(name = "reference_id")
    private Long referenceId;

    @Lob
    @Column(name = "old_data")
    private String oldData;

    @Lob
    @Column(name = "new_data")
    private String newData;
}