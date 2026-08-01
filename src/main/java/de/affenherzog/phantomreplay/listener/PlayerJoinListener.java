package de.affenherzog.phantomreplay.listener;

import de.affenherzog.phantomreplay.player.PhantomPlayer;
import de.affenherzog.phantomreplay.player.PhantomPlayerManager;
import de.affenherzog.phantomreplay.player.PlayerRepository;
import de.affenherzog.phantomreplay.replay.ReplayRepository;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.UUID;

public class PlayerJoinListener implements Listener {

    private final ReplayRepository replayRepository;
    private final PlayerRepository playerRepository;
    private final PhantomPlayerManager phantomPlayerManager;
    private final Plugin plugin;


    public PlayerJoinListener(Plugin plugin, PhantomPlayerManager phantomPlayerManager, ReplayRepository replayRepository, PlayerRepository playerRepository) {
        this.plugin = plugin;
        this.phantomPlayerManager = phantomPlayerManager;
        this.replayRepository = replayRepository;
        this.playerRepository = playerRepository;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();

        final PhantomPlayer phantomPlayer = new PhantomPlayer(uuid, new ArrayList<>());
        phantomPlayerManager.addPhantomPlayer(phantomPlayer);

        playerRepository.savePlayer(phantomPlayer)
                .thenCompose(_ -> replayRepository.loadReplays(uuid))
                .whenComplete((replays, exception) -> {
                    if (exception != null) {
                        plugin.getSLF4JLogger().error("Datenbank-Fehler beim Login: {}", exception.getMessage());
                        return;
                    }

                    Bukkit.getScheduler().runTask(plugin, () -> phantomPlayerManager.getPhantomPlayer(uuid)
                            .ifPresent((player) -> player.addReplays(replays)
                            ));
                });
    }


}
