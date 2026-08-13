package de.affenherzog.phantomreplay.listener;

import de.affenherzog.phantomreplay.application.PlayerConnectionService;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final PlayerConnectionService playerConnectionService;

    public PlayerQuitListener(PlayerConnectionService playerConnectionService) {
        this.playerConnectionService = playerConnectionService;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        playerConnectionService.logout(event.getPlayer().getUniqueId());
    }


}
