package de.affenherzog.phantomreplay.session;

import lombok.extern.slf4j.Slf4j;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@Slf4j
public class PlayerJoinListener implements Listener {

    private final PlayerLoginService playerLoginService;

    public PlayerJoinListener(PlayerLoginService playerLoginService) {
        this.playerLoginService = playerLoginService;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        playerLoginService.loadPlayerData(event.getPlayer().getUniqueId());
    }

}
