package com.enterprise.portfolio.health;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.AbstractHealthIndicator;
import org.springframework.boot.actuate.health.Health;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

@Component
public class DatabaseHealthIndicator extends AbstractHealthIndicator {

    private static final String DEFAULT_QUERY = "SELECT 1";
    
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseHealthIndicator(DataSource dataSource, JdbcTemplate jdbcTemplate) {
        super("Database health check failed");
        this.dataSource = dataSource;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    protected void doHealthCheck(Health.Builder builder) {
        try (Connection connection = dataSource.getConnection()) {
            // Check if connection is valid
            boolean isValid = connection.isValid(5); // 5 seconds timeout
            
            // Execute a simple query to verify database is responsive
            String result = jdbcTemplate.queryForObject(DEFAULT_QUERY, String.class);
            
            // Get connection metadata
            String dbName = connection.getMetaData().getDatabaseProductName();
            String dbVersion = connection.getMetaData().getDatabaseProductVersion();
            
            // Build health details
            Map<String, Object> details = new HashMap<>();
            details.put("database", dbName);
            details.put("version", dbVersion);
            details.put("connection_valid", isValid);
            details.put("query_successful", "1".equals(result));
            
            // Set health status
            if (isValid && "1".equals(result)) {
                builder.up()
                    .withDetails(details);
            } else {
                builder.down()
                    .withDetails(details);
            }
            
        } catch (SQLException e) {
            builder.down()
                .withDetail("error", e.getMessage())
                .withException(e);
        }
    }
}
