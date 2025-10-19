package com.example.covault.repositories;

import com.example.covault.entities.ZZZActivityLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZZZActivityLogsRepository extends JpaRepository<ZZZActivityLogs, Long> {
}
