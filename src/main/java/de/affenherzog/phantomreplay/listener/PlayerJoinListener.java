package de.affenherzog.phantomreplay.listener;

import de.affenherzog.phantomreplay.playback.*;
import de.affenherzog.phantomreplay.player.PhantomPlayer;
import de.affenherzog.phantomreplay.player.PhantomPlayerManager;
import de.affenherzog.phantomreplay.player.PlayerRepository;
import de.affenherzog.phantomreplay.replay.Replay;
import de.affenherzog.phantomreplay.replay.ReplayRepository;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
public class PlayerJoinListener implements Listener {

    private final PlayerRepository playerRepository;
    private final ReplayRepository replayRepository;
    private final PlaybackRepository playbackRepository;
    private final PhantomPlayerManager phantomPlayerManager;
    private final PlaybackManager playbackManager;
    private final Plugin plugin;


    public PlayerJoinListener(Plugin plugin, PhantomPlayerManager phantomPlayerManager, ReplayRepository replayRepository, PlayerRepository playerRepository, PlaybackRepository playbackRepository, PlaybackManager playbackManager) {
        this.plugin = plugin;
        this.phantomPlayerManager = phantomPlayerManager;
        this.replayRepository = replayRepository;
        this.playerRepository = playerRepository;
        this.playbackRepository = playbackRepository;
        this.playbackManager = playbackManager;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();

        final PhantomPlayer phantomPlayer = new PhantomPlayer(uuid, playbackManager);
        phantomPlayerManager.addPhantomPlayer(phantomPlayer);

        playerRepository.savePlayer(phantomPlayer)
                .thenCompose(_ -> replayRepository.loadReplays(uuid))
                .thenCompose(replays -> {
                    Bukkit.getScheduler().runTask(plugin, () ->
                            phantomPlayerManager.getPhantomPlayer(uuid).ifPresent(ph -> ph.addReplays(replays)));

                    Map<Integer, Replay> replayMap;
                    replayMap = replays.stream().collect(Collectors.toMap(Replay::id, it -> it));
                    return playbackRepository.loadAllPlaybackSessions(replayMap);
                })
                .thenAccept(playbackSessionModels ->
                        Bukkit.getScheduler().runTask(plugin, () ->
                                phantomPlayerManager.getPhantomPlayer(uuid).ifPresent(_ -> {
                                    List<PlaybackSessionRunner> runners = playbackSessionModels.stream()
                                            .map(it -> new PlaybackSessionRunner(it, uuid))
                                            .toList();

                                    playbackManager.addSessions(runners);
                                })
                        ))
                .exceptionally(exception -> {
                    plugin.getSLF4JLogger().error("Datenbank-Fehler beim Login: {}", exception.getMessage());
                    return null;
                });
    }

}
