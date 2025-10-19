package com.example.covault.repositories;

import com.example.covault.entities.ZZZActivityMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZZZActivityMessagesRepository extends JpaRepository<ZZZActivityMessages, String> {
}
