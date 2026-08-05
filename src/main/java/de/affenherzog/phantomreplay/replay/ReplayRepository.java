package de.affenherzog.phantomreplay.replay;

import com.zaxxer.hikari.HikariDataSource;
import de.affenherzog.phantomreplay.database.AbstractRepository;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ReplayRepository extends AbstractRepository {

    public ReplayRepository(HikariDataSource dataSource, ComponentLogger componentLogger) {
        super(dataSource, componentLogger);
    }

    public CompletableFuture<Replay> saveReplay(Replay replay) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "INSERT INTO replay (name, uuid, data) VALUES (?, ?, ?)";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, replay.name());
                stmt.setString(2, replay.uuid().toString());
                stmt.setString(3, ReplaySerializer.toJson(replay.keyFrames()));

                stmt.executeUpdate();

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newId = generatedKeys.getInt(1);
                        return new Replay(newId, replay.name(), replay.uuid(), replay.keyFrames());
                    } else {
                        throw new SQLException("Datenbank hat keine ID generiert!");
                    }
                }

            } catch (Exception e) {
                logError("Fehler beim Speichern: {}", e.getMessage());
                return replay;
            }
        });
    }

    public CompletableFuture<List<Replay>> loadReplays(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT id, uuid, name, data, created_at FROM replay WHERE uuid = ?";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, playerId.toString());

                ResultSet rs = stmt.executeQuery();
                List<Replay> replays = new ArrayList<>();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    String json = rs.getString("data");

                    List<KeyFrame> keyFrames = ReplaySerializer.fromJson(json);

                    Replay replay = new Replay(id, name, uuid, keyFrames);
                    replays.add(replay);
                }

                return replays;

            } catch (Exception e) {
                logError("Fehler beim Laden der Replay-Daten: {}", e.getMessage());
                return Collections.emptyList();
            }
        });
    }

    public CompletableFuture<Void> updateReplayName(int replayId, String newName) {
        return CompletableFuture.runAsync(() -> {
            String sql = "UPDATE replay SET name = ? WHERE id = ?";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, newName);
                stmt.setInt(2, replayId);

                int rowsAffected = stmt.executeUpdate();

                if (rowsAffected == 0) {
                    logWarn("Konnte den Namen für Replay-ID {} nicht ändern, da es nicht existiert.", replayId);
                }

            } catch (Exception e) {
                logError("Fehler beim Aktualisieren des Replay-Namens (ID: {}): {}", replayId, e.getMessage());
                throw new RuntimeException("Datenbankfehler beim Umbenennen", e);
            }
        });
    }


}
