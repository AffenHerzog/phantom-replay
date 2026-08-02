package de.affenherzog.phantomreplay.playback;

import com.zaxxer.hikari.HikariDataSource;
import de.affenherzog.phantomreplay.database.AbstractRepository;
import de.affenherzog.phantomreplay.replay.Replay;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class PlaybackRepository extends AbstractRepository {

    public PlaybackRepository(HikariDataSource dataSource, ComponentLogger componentLogger) {
        super(dataSource, componentLogger);
    }

    public CompletableFuture<Void> updatePlaybackSession(PlaybackSessionModel model) {
        return CompletableFuture.runAsync(() -> {
            String sql = "UPDATE playback_session SET active = ?, scope = ? WHERE id = ?";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setBoolean(1, model.isActive());
                stmt.setString(2, model.getVisibilityScope().name());
                stmt.setInt(3, model.getId());

                stmt.executeUpdate();

            } catch (SQLException e) {
                logError("Fehler beim Aktualisieren der Playback-Sitzung {}", model.getId(), e);
            }
        });
    }

    public CompletableFuture<List<PlaybackSessionModel>> loadAllPlaybackSessions(Map<Integer, Replay> availableReplays) {
        return CompletableFuture.supplyAsync(() -> {
            List<PlaybackSessionModel> sessions = new ArrayList<>();

            if (availableReplays.isEmpty()) {
                return sessions;
            }

            String placeholders = String.join(",", Collections.nCopies(availableReplays.size(), "?"));
            String sql = "SELECT id, replay_id, active, scope FROM playback_session WHERE replay_id IN (" + placeholders + ")";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                int index = 1;
                for (Integer replayId : availableReplays.keySet()) {
                    stmt.setInt(index++, replayId);
                }

                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        int id = rs.getInt("id");
                        int replayId = rs.getInt("replay_id");
                        boolean active = rs.getBoolean("active");

                        String scopeStr = rs.getString("scope");
                        VisibilityScope scope = VisibilityScope.valueOf(scopeStr);

                        Replay replay = availableReplays.get(replayId);

                        if (replay != null) {
                            sessions.add(new PlaybackSessionModel(id, replay, active, scope));
                        }
                    }
                }

            } catch (SQLException | IllegalArgumentException e) {
                logError("Fehler beim Laden der Playback-Sitzungen", e);
            }

            return sessions;
        });
    }

    public CompletableFuture<PlaybackSessionModel> savePlaybackSession(PlaybackSessionModel model) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "INSERT INTO playback_session (replay_id, active, scope) VALUES (?, ?, ?)";

            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setInt(1, model.getReplay().id());
                stmt.setBoolean(2, model.isActive());
                stmt.setString(3, model.getVisibilityScope().name());

                stmt.executeUpdate();

                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return new PlaybackSessionModel(
                                rs.getInt(1),
                                model.getReplay(),
                                model.isActive(),
                                model.getVisibilityScope()
                        );
                    } else {
                        throw new SQLException("Datenbank hat keine ID generiert!");
                    }
                }

            } catch (SQLException e) {
                logError("Fehler beim Speichern der Playback-Sitzung", e);
                return model;
            }
        });
    }
}