package de.affenherzog.phantomReplay.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.Statement;

public class DatabaseManager {

    private final Plugin plugin;
    private final FileConfiguration config;
    private final Logger log;

    private HikariDataSource dataSource;

    public DatabaseManager(Plugin plugin, Logger log) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
        this.log = log;
    }

    public boolean connect() {
        try {
            HikariConfig config = getHikariConfig();

            config.addDataSourceProperty("cachePrepStmts", "true");
            config.addDataSourceProperty("prepStmtCacheSize", "250");
            config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
            config.addDataSourceProperty("useServerPrepStmts", "true");

            dataSource = new HikariDataSource(config);
            return true;
        } catch (Exception e) {
            log.error("Datenbank-Fehler: {}", e.getMessage());

            if (e.getCause() != null) {
                log.error("Ursache: {}", e.getCause().getMessage());
            }
            return false;
        }
    }

    private HikariConfig getHikariConfig() {
        HikariConfig hikariConfig = new HikariConfig();

        hikariConfig.setDriverClassName("org.mariadb.jdbc.Driver");

        String url = String.format("jdbc:mariadb://%s:%s/%s",
                config.getString("database.host"),
                config.getInt("database.port"),
                config.getString("database.name"));

        hikariConfig.setJdbcUrl(url);
        hikariConfig.setUsername(config.getString("database.user"));
        hikariConfig.setPassword(config.getString("database.password"));
        hikariConfig.setMaximumPoolSize(config.getInt("database.maxPoolSize", 10));
        hikariConfig.setMinimumIdle(config.getInt("database.minPoolSize", 2));
        hikariConfig.setConnectionTimeout(10000);
        hikariConfig.setMaxLifetime(1800000);

        return hikariConfig;
    }

    public void disconnect() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public boolean initSchema() {
        if (dataSource == null || dataSource.isClosed()) {
            log.error("Konnte Schema nicht erstellen: Keine aktive Datenbankverbindung!");
            return false;
        }

        try (InputStream in = plugin.getResource("schema.sql")) {
            if (in == null) {
                log.error("Das Datenbankschema konnte nicht erstellt werden! SQL-Datei konnte nicht in den Ressourcen gefunden werden!");
                return false;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line).append("\n");
            }

            String[] queries = builder.toString().split(";");

            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {

                for (String query : queries) {
                    if (query.trim().isEmpty()) {
                        continue;
                    }
                    stmt.execute(query);
                }
            }
            return true;

        } catch (Exception e) {
            log.error("Fehler beim Ausführen der schema.sql: {}", e.getMessage());
            return false;
        }
    }

}