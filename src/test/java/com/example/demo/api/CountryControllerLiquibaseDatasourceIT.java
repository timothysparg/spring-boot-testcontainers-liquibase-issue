package com.example.demo.api;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseDataSource;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.zaxxer.hikari.HikariDataSource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
public class CountryControllerLiquibaseDatasourceIT {

    @TestConfiguration
    @AutoConfigureBefore(LiquibaseAutoConfiguration.class)
    static class TestConfig {

        @Autowired
        DataSource dataSource;

        @LiquibaseDataSource
        @Bean
        public DataSource liquibaseDataSource() {
            return dataSource;
        }
    }

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
                    new PostgreSQLContainer<>("postgres:15.2-alpine")
                                    .withUsername("dummy")
                                    .withPassword("dummy");

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnProducts() throws Exception {
        mockMvc.perform(get("/api/countries"))
                        .andExpect(status().isOk());
    }
}
