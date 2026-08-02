package de.affenherzog.phantomreplay.database;

import com.zaxxer.hikari.HikariDataSource;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.sql.Connection;
import java.sql.SQLException;

public class AbstractRepository {

    private final HikariDataSource dataSource;
    private final ComponentLogger componentLogger;

    public AbstractRepository(HikariDataSource dataSource, ComponentLogger componentLogger) {
        this.dataSource = dataSource;
        this.componentLogger = componentLogger;
    }

    protected Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }

    protected void logInfo(String message, Object... arguments) {
        String prefixedMessage = "[" + getClass().getSimpleName() + "] " + message;
        componentLogger.info(prefixedMessage, arguments);
    }

    protected void logWarn(String message, Object... arguments) {
        String prefixedMessage = "[" + getClass().getSimpleName() + "] " + message;
        componentLogger.warn(prefixedMessage, arguments);
    }

    protected void logError(String message, Object... arguments) {
        String prefixedMessage = "[" + getClass().getSimpleName() + "] " + message;
        componentLogger.error(prefixedMessage, arguments);
    }

}
