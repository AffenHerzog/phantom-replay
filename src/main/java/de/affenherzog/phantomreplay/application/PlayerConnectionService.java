package de.affenherzog.phantomreplay.application;

import de.affenherzog.phantomreplay.playback.PlaybackManager;
import de.affenherzog.phantomreplay.playback.PlaybackRepository;
import de.affenherzog.phantomreplay.playback.PlaybackSessionRunner;
import de.affenherzog.phantomreplay.player.PhantomPlayer;
import de.affenherzog.phantomreplay.player.PhantomPlayerManager;
import de.affenherzog.phantomreplay.player.PlayerRepository;
import de.affenherzog.phantomreplay.record.RecordingManager;
import de.affenherzog.phantomreplay.replay.Replay;
import de.affenherzog.phantomreplay.replay.ReplayRepository;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PlayerConnectionService {

    private final Plugin plugin;

    private final PlayerRepository playerRepository;
    private final ReplayRepository replayRepository;
    private final PlaybackRepository playbackRepository;

    private final PhantomPlayerManager phantomPlayerManager;
    private final PlaybackManager playbackManager;
    private final RecordingManager recordingManager;
    private final ReplayManagementService replayManagementService;


    public void loadPlayerData(UUID uuid) {
        final PhantomPlayer phantomPlayer = new PhantomPlayer(uuid);
        phantomPlayerManager.addPhantomPlayer(phantomPlayer);

        playerRepository.savePlayer(phantomPlayer)
                .thenCompose(_ -> loadReplaysAsync(uuid, phantomPlayer))
                .thenCompose(playbackSessionModels -> loadPlaybacksAsync(uuid, playbackSessionModels))
                .exceptionally(exception -> {
                    plugin.getSLF4JLogger().error("Datenbank-Fehler beim Login: {}", exception.getMessage());
                    return null;
                });
    }

    private CompletableFuture<List<Replay>> loadReplaysAsync(UUID uuid, PhantomPlayer player) {
        return replayRepository.loadReplays(uuid).thenApply(replays -> {
            Bukkit.getScheduler().runTask(plugin, () -> player.addReplays(replays));
            return replays;
        });
    }

    private CompletableFuture<Void> loadPlaybacksAsync(UUID uuid, List<Replay> replays) {
        Map<Integer, Replay> replayMap = replays.stream().collect(Collectors.toMap(Replay::id, it -> it));

        return playbackRepository.loadAllPlaybackSessions(replayMap).thenAccept(models ->
                Bukkit.getScheduler().runTask(plugin, () ->
                        phantomPlayerManager.getPhantomPlayer(uuid).ifPresent(p -> {
                            List<PlaybackSessionRunner> runners = models.stream()
                                    .map(it -> new PlaybackSessionRunner(it, uuid))
                                    .toList();
                            playbackManager.registerSessions(runners);
                        })));
    }

    public void logout(UUID uuid) {
        recordingManager.stopRecording(uuid);
        playbackManager.removeAllSessions(uuid);
        replayManagementService.removeRenaming(uuid);
        phantomPlayerManager.getPhantomPlayer(uuid).ifPresent(
                (_ -> phantomPlayerManager.removePhantomPlayer(uuid)));
    }

}
