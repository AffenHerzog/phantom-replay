package de.affenherzog.phantomreplay.session;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    private final PlayerLogoutService playerLogoutService;

    public PlayerQuitListener(PlayerLogoutService playerLogoutService) {
        this.playerLogoutService = playerLogoutService;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        playerLogoutService.logout(event.getPlayer().getUniqueId());
    }


}
