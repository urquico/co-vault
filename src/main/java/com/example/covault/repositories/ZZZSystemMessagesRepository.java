package com.example.covault.repositories;

import com.example.covault.entities.ZZZSystemMessages;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZZZSystemMessagesRepository extends JpaRepository<ZZZSystemMessages, String> {

}
