package de.affenherzog.phantomreplay.listener;

import de.affenherzog.phantomreplay.player.PhantomPlayerManager;
import de.affenherzog.phantomreplay.record.RecordingManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    private final PhantomPlayerManager phantomPlayerManager;
    private final RecordingManager recordingManager;

    public PlayerQuitListener(PhantomPlayerManager phantomPlayerManager, RecordingManager recordingManager) {
        this.phantomPlayerManager = phantomPlayerManager;
        this.recordingManager = recordingManager;
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        final UUID uuid = event.getPlayer().getUniqueId();
        phantomPlayerManager.getPhantomPlayer(uuid)
                .ifPresent((_ -> phantomPlayerManager.removePhantomPlayer(uuid)));
        recordingManager.stopRecording(uuid);
    }


}
