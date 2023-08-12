package com.example.demo.api;

import java.time.Duration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
                properties = {"spring.datasource.hikari.connectionTimeout=30000 "})
@Testcontainers
@AutoConfigureMockMvc
public class CountryControllerIT {

    @Container
    @ServiceConnection
    static PostgreSQLContainer<?> postgres =
                    new PostgreSQLContainer<>("postgres:15.2-alpine")
                                    .withUsername("dummy")
                                    .withPassword("dummy")
                                    //.withMinimumRunningDuration(Duration.ofSeconds(5))
                                    ;

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnProducts() throws Exception {
        mockMvc.perform(get("/api/countries"))
                        .andExpect(status().isOk());
    }
}
