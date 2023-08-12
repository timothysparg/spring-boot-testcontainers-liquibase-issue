package com.example.demo.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.demo.entity.Country;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Testcontainers
public class CountryRepositoryTest {

    // Starting a PostgreSQL container
    @Container
    static PostgreSQLContainer<?> databaseContainer = new PostgreSQLContainer<>("postgres:15.2-alpine")
                    .withDatabaseName("testdb")
                    .withUsername("user")
                    .withPassword("password");

    @Autowired
    private CountryRepository countryRepository;

    @DynamicPropertySource
    static void setDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", databaseContainer::getJdbcUrl);
        registry.add("spring.datasource.username", databaseContainer::getUsername);
        registry.add("spring.datasource.password", databaseContainer::getPassword);
    }

    @Test
    public void whenFindByName_thenShouldReturnCountry() {
        Country liechtenstein = new Country(null, "Liechtenstein", "LI", "LIE", "🇱🇮");
        countryRepository.save(liechtenstein);

        Country found = countryRepository.findByName("Liechtenstein");
        assertThat(found.getName()).isEqualTo(liechtenstein.getName());
    }
}