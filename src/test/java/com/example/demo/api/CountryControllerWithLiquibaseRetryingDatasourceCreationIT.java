package com.example.demo.api;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import liquibase.integration.spring.SpringLiquibase;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
public class CountryControllerWithLiquibaseRetryingDatasourceCreationIT {

    private static final Logger logger = LoggerFactory.getLogger(
                    CountryControllerWithLiquibaseRetryingDatasourceCreationIT.class);

    @TestConfiguration
    @AutoConfigureBefore(LiquibaseAutoConfiguration.class)
    static class TestConfig {

        @Bean
        public SpringLiquibase springLiquibase(DataSource ds)  {

            int attemptCount = 0;
            int maxAttempts = 20;
            Connection c = null;

            while (attemptCount < maxAttempts) {
                try {
                    c = ds.getConnection();
                    if (c != null) {
                        break;
                    }
                } catch (SQLException e) {
                    attemptCount++;
                    logger.warn("Datasource not available. Attempt " + attemptCount + " of " + maxAttempts);

                    if (attemptCount >= maxAttempts) {
                        logger.error("Failed to get a connection after " + maxAttempts + " attempts. Exiting.");
                        return null;
                    }
                    // initially had the below line in with higher values, but ended up removing it entirely
                    // the conclusion that I draw is that the first call always fails ??
                    // Thread.sleep(1);
                }
            }

            var liquibase = new SpringLiquibase();
            liquibase.setDataSource(ds);
            liquibase.setChangeLog("classpath:db/changelog/db.changelog-master.yaml");
            return liquibase;
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
