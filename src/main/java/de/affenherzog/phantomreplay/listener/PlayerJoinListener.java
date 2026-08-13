package de.affenherzog.phantomreplay.listener;

import de.affenherzog.phantomreplay.application.PlayerConnectionService;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@Slf4j
public class PlayerJoinListener implements Listener {

    private final PlayerConnectionService playerConnectionService;

    public PlayerJoinListener(PlayerConnectionService playerConnectionService) {
        this.playerConnectionService = playerConnectionService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        playerConnectionService.loadPlayerData(event.getPlayer().getUniqueId());
    }

}
