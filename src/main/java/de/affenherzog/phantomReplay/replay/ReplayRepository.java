package de.affenherzog.phantomReplay.replay;

import com.google.gson.reflect.TypeToken;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ReplayRepository {

    private final HikariDataSource dataSource;
    private final Logger log;

    public ReplayRepository(HikariDataSource dataSource, Logger log) {
        this.dataSource = dataSource;
        this.log = log;
    }

    public CompletableFuture<Replay> saveReplay(Replay replay) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "INSERT INTO phantom_replays (name, uuid, data) VALUES (?, ?, ?)";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

                stmt.setString(1, replay.name());
                stmt.setString(2, replay.uuid().toString());
                stmt.setString(3, ReplaySerializer.GSON.toJson(replay.frames()));

                stmt.executeUpdate();

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int newId = generatedKeys.getInt(1);
                        return new Replay(newId, replay.name(), replay.uuid(), replay.frames());
                    } else {
                        throw new SQLException("Datenbank hat keine ID generiert!");
                    }
                }

            } catch (Exception e) {
                log.error("Fehler beim Speichern: {}", e.getMessage());
                return replay;
            }
        });
    }

    public CompletableFuture<List<Replay>> loadReplays(UUID playerId) {
        return CompletableFuture.supplyAsync(() -> {
            String sql = "SELECT * FROM phantom_replays WHERE uuid = ?";

            try (Connection conn = dataSource.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, playerId.toString());

                ResultSet rs = stmt.executeQuery();
                List<Replay> replays = new ArrayList<>();

                Type frameListType = new TypeToken<List<Frame>>() {}.getType();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    UUID uuid = UUID.fromString(rs.getString("uuid"));
                    String json = rs.getString("data");

                    List<Frame> frames = ReplaySerializer.GSON.fromJson(json, frameListType);

                    Replay replay = new Replay(id, name, uuid, frames);
                    replays.add(replay);
                }

                return replays;

            } catch (Exception e) {
                log.error("Fehler beim Laden der Replay-Daten: {}", e.getMessage());
                return Collections.emptyList();
            }
        });
    }


}
