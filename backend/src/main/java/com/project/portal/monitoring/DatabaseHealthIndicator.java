package com.project.portal.monitoring;

import java.sql.Connection;
import java.sql.SQLException;
import javax.sql.DataSource;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {

    private final DataSource dataSource;

    @Override
    public Health health() {
        try (Connection connection = dataSource.getConnection()) {
            boolean valid = connection.isValid(2);
            if (valid) {
                return Health.up()
                        .withDetail("database", "UP")
                        .build();
            }
            return Health.down()
                    .withDetail("database", "DOWN")
                    .build();
        } catch (SQLException e) {
            return Health.down(e)
                    .withDetail("database", "DOWN")
                    .build();
        }
    }
}
