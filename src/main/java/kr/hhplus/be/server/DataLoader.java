package kr.hhplus.be.server;

import lombok.extern.slf4j.Slf4j;
import org.flywaydb.core.Flyway;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Slf4j
@Profile("test")
public class DataLoader {

    @Bean
    public CommandLineRunner run(DataSeeder seeder) {
        return args -> {
            log.info("🔁 Running custom test data seeding");
            seeder.run();
        };
    }
}