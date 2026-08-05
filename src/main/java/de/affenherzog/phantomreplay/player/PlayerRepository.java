package de.affenherzog.phantomreplay.player;

import com.zaxxer.hikari.HikariDataSource;
import de.affenherzog.phantomreplay.database.AbstractRepository;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.CompletableFuture;

public class PlayerRepository extends AbstractRepository {

    public PlayerRepository(HikariDataSource dataSource, ComponentLogger log) {
        super(dataSource, log);
    }

    public CompletableFuture<Void> savePlayer(PhantomPlayer player) {
        return CompletableFuture.runAsync(() -> {

            String sql = "INSERT IGNORE INTO player (uuid) VALUES (?);";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, player.getUuid().toString());

                stmt.executeUpdate();

            } catch (SQLException e) {
                logError("Fehler beim Speichern des Spielers {}", player.getUuid(), e);
            }
        });
    }
}
