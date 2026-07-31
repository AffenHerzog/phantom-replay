package de.affenherzog.phantomReplay.listener;

import de.affenherzog.phantomReplay.player.PhantomPlayerManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    private final PhantomPlayerManager phantomPlayerManager;

    public PlayerQuitListener(PhantomPlayerManager phantomPlayerManager) {
        this.phantomPlayerManager = phantomPlayerManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        phantomPlayerManager.getPhantomPlayer(uuid)
                .ifPresent((_ -> phantomPlayerManager.removePhantomPlayer(uuid)));

    }


}
