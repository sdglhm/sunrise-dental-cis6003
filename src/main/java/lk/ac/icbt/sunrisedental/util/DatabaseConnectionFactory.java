package lk.ac.icbt.sunrisedental.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Central place for creating JDBC connections. Configuration is supplied in
 * database.properties, which is intentionally excluded from version control.
 */
public final class DatabaseConnectionFactory {
    private static final DatabaseConnectionFactory INSTANCE = new DatabaseConnectionFactory();
    private final Properties properties = new Properties();

    private DatabaseConnectionFactory() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("database.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to read database.properties", exception);
        }
    }

    public static DatabaseConnectionFactory getInstance() {
        return INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(
                requiredProperty("db.url"),
                requiredProperty("db.username"),
                requiredProperty("db.password"));
    }

    private String requiredProperty(String key) {
        String value = properties.getProperty(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException("Missing required database setting: " + key);
        }
        return value;
    }
}
