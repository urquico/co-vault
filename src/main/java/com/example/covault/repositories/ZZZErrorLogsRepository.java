package com.example.covault.repositories;

import com.example.covault.entities.ZZZErrorLogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZZZErrorLogsRepository extends JpaRepository<ZZZErrorLogs, Long> {
}
