package de.affenherzog.phantomReplay.player;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class PlayerRepository {

    private final HikariDataSource dataSource;
    private final Logger log;

    public PlayerRepository(HikariDataSource dataSource, Logger log) {
        this.dataSource = dataSource;
        this.log = log;
    }


    public CompletableFuture<Void> savePlayer(PhantomPlayer player) {
        return CompletableFuture.runAsync(() -> {

            String sql = "INSERT IGNORE INTO phantom_players (uuid) VALUES (?);";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, player.getUuid().toString());

                stmt.executeUpdate();

            } catch (SQLException e) {
                log.error("Fehler beim Speichern des Spielers {}: {}", player.getUuid(), e.getMessage());
            }
        });
    }
}
