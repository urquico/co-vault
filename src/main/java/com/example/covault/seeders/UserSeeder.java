package com.example.covault.seeders;

import com.example.covault.entities.Users;
import com.example.covault.interfaces.DatabaseSeeder;
import com.example.covault.repositories.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class UserSeeder implements DatabaseSeeder {
    @Autowired
    private UserRepository userRepository;

    @Value("${spring.app.seed.user}")
    private boolean isSeed;

    @Override
    public void seed() {
        if (!isSeed) {
            log.info("🚫 User seeding skipped (spring.app.seed.user=false)");
            return;
        }

        if (userRepository.count() == 0) {
            log.info("🌱 Seeding initial User...");
            Users user = new Users();
            user.setEmail("urquico.dev@gmail.com");
            userRepository.save(user);
            log.info("✅ User seeding complete.");
        } else {
            log.info("ℹ️ Users already exist, skipping seeding.");
        }
    }
}
