package integration;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@TestConfiguration
public class Config {

    @Bean
    public DataGenerator dataGenerator() {
        return new DataGenerator();
    }

    @PostConstruct
    public void setupDatabase() {
    }

    @PreDestroy
    public void cleanDatabase() {
    }
}
