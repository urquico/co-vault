package com.example.covault.seeders;

import com.example.covault.interfaces.DatabaseSeeder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class DatabaseSeederRunner implements CommandLineRunner {

    private final List<DatabaseSeeder> seeders;

    @Value("${spring.app.seed.all}")
    private boolean isSeed;

    public DatabaseSeederRunner(List<DatabaseSeeder> seeders) {
        this.seeders = seeders;
    }

    @Override
    public void run(String... args) {
        if (!isSeed) {
            log.info("ℹ️ Database seeding skipped (spring.app.seed.all=false)");
            return;
        }

        log.info("🌱 Running {} database seeders...", seeders.size());
        seeders.forEach(seeder -> {
            log.info("➡️ Running seeder: {}", seeder.getClass().getSimpleName());
            seeder.seed();
        });
        log.info("✅ All database seeders completed successfully.");
    }
}